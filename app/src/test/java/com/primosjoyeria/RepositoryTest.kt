package com.primosjoyeria.data.repository

import com.primosjoyeria.R
import com.primosjoyeria.data.local.ProductoDao
import com.primosjoyeria.data.local.UserDao
import com.primosjoyeria.data.model.CartItem
import com.primosjoyeria.data.model.Product
import com.primosjoyeria.data.model.User
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogRepositoryRoomTest {

    @MockK
    lateinit var productoDao: ProductoDao

    @MockK
    lateinit var userDao: UserDao

    private lateinit var repo: CatalogRepositoryRoom

    @BeforeEach
    fun setup() {
        // relaxUnitFun = true -> no reclama por funciones que devuelven Unit
        MockKAnnotations.init(this, relaxUnitFun = true)
        repo = CatalogRepositoryRoom(productoDao, userDao)
    }

    @Test
    fun `seedIfEmpty inserta productos iniciales cuando no hay productos`() = runTest {
        coEvery { productoDao.countProductos() } returns 0

        repo.seedIfEmpty()

        coVerify(exactly = 1) {
            productoDao.insertProductos(withArg { lista ->
                lista.shouldHaveSize(3)
                lista[0].nombre.shouldBe("Aros Perla")
                lista[1].nombre.shouldBe("Collar Plata 925")
                lista[2].nombre.shouldBe("Pulsera Acero")
            })
        }
    }

    @Test
    fun `seedIfEmpty no inserta nada cuando ya hay productos`() = runTest {
        coEvery { productoDao.countProductos() } returns 5

        repo.seedIfEmpty()

        coVerify(exactly = 0) { productoDao.insertProductos(any()) }
    }

    @Test
    fun `agregarAlCarrito no inserta nuevo item si ya existia en carrito`() = runTest {
        val producto = Product(
            id = 10,
            nombre = "Anillo de prueba",
            precio = 9990,
            imagenRes = R.drawable.logo
        )

        coEvery { productoDao.actualizarCantidadNoNegativa(producto.id, +1) } returns 1

        repo.agregarAlCarrito(producto)

        coVerify(exactly = 1) {
            productoDao.actualizarCantidadNoNegativa(producto.id, +1)
        }
        coVerify(exactly = 0) {
            productoDao.upsertCarrito(any())
        }
    }

    @Test
    fun `agregarAlCarrito inserta item nuevo cuando no existia en carrito`() = runTest {
        val producto = Product(
            id = 20,
            nombre = "Collar prueba",
            precio = 19990,
            imagenRes = R.drawable.logo
        )

        coEvery { productoDao.actualizarCantidadNoNegativa(producto.id, +1) } returns 0

        repo.agregarAlCarrito(producto)

        coVerify(exactly = 1) {
            productoDao.upsertCarrito(withArg { item: CartItem ->
                item.productId.shouldBe(producto.id)
                item.nombre.shouldBe(producto.nombre)
                item.precio.shouldBe(producto.precio)
                item.cantidad.shouldBe(1)
            })
        }
    }

    @Test
    fun `cambiarCantidad elimina del carrito cuando cantidad queda en cero`() = runTest {
        val productId = 30

        coEvery { productoDao.actualizarCantidadNoNegativa(productId, -1) } returns 1
        coEvery { productoDao.obtenerCantidad(productId) } returns 0

        repo.cambiarCantidad(productId, -1)

        coVerify { productoDao.eliminarDelCarrito(productId) }
    }

    @Test
    fun `cambiarCantidad no elimina del carrito cuando cantidad sigue positiva`() = runTest {
        val productId = 40

        coEvery { productoDao.actualizarCantidadNoNegativa(productId, -1) } returns 1
        coEvery { productoDao.obtenerCantidad(productId) } returns 3

        repo.cambiarCantidad(productId, -1)

        coVerify(exactly = 0) { productoDao.eliminarDelCarrito(any()) }
    }

    @Test
    fun `registrarUsuario inserta usuario cuando correo no existe`() = runTest {
        val correo = "test@correo.cl"
        val pass = "1234"
        val sexo = "f"
        val edad = 25

        coEvery { userDao.countByEmail(correo.trim()) } returns 0

        val result = repo.registrarUsuario(correo, pass, sexo, edad)

        result.isSuccess.shouldBe(true)

        coVerify {
            userDao.insert(withArg { user: User ->
                user.correo.shouldBe(correo.trim())
                user.pass.shouldBe(pass)
                user.sexo.shouldBe(sexo.trim().uppercase())
                user.edad.shouldBe(edad)
            })
        }
    }

    @Test
    fun `registrarUsuario falla si el correo ya existe y no inserta`() = runTest {
        val correo = "yaexiste@correo.cl"

        coEvery { userDao.countByEmail(correo.trim()) } returns 1

        val result = repo.registrarUsuario(
            correo = correo,
            password = "pwd",
            sexo = "M",
            edad = 30
        )

        result.isFailure.shouldBe(true)

        coVerify(exactly = 0) {
            userDao.insert(any())
        }
    }

    @Test
    fun `verificarCredenciales retorna true cuando validate es mayor que cero`() = runTest {
        val correo = "user@test.cl"
        val password = "pwd"

        coEvery { userDao.validate(correo.trim(), password) } returns 1

        val ok = repo.verificarCredenciales(correo, password)

        ok.shouldBe(true)
    }

    @Test
    fun `verificarCredenciales retorna false cuando validate es cero`() = runTest {
        val correo = "user@test.cl"
        val password = "pwd"

        coEvery { userDao.validate(correo.trim(), password) } returns 0

        val ok = repo.verificarCredenciales(correo, password)

        ok.shouldBe(false)
    }
}
