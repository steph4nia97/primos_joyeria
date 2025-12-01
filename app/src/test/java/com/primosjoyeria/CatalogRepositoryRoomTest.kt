package com.primosjoyeria
import com.primosjoyeria.data.local.ProductoDao
import com.primosjoyeria.data.local.UserDao
import com.primosjoyeria.data.remote.auth.ApiService
import com.primosjoyeria.data.remote.auth.ProductDto
import com.primosjoyeria.data.repository.CatalogRepositoryRoom
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.confirmVerified
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogRepositoryRoomTest {

    @MockK
    lateinit var productoDao: ProductoDao

    @MockK
    lateinit var userDao: UserDao

    @MockK
    lateinit var apiService: ApiService

    private lateinit var repo: CatalogRepositoryRoom

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        repo = CatalogRepositoryRoom(
            dao = productoDao,   //
            userDao = userDao,
            api = apiService
        )
    }


    @Test
    fun `seedIfEmpty inserta productos desde backend cuando esta vacio`() = runTest {
        // backend devuelve 2 productos
        val remotos = listOf(
            ProductDto(id = 1, nombre = "Aros", precio = 1000, imagenUrl = "url1"),
            ProductDto(id = 2, nombre = "Collar", precio = 2000, imagenUrl = "url2")
        )

        coEvery { apiService.getProductos() } returns remotos
        coEvery { productoDao.borrarTodo() } just runs
        coEvery { productoDao.insertProductos(any()) } just runs

        repo.seedIfEmpty()

        coVerify(exactly = 1) { apiService.getProductos() }
        coVerify(exactly = 1) { productoDao.borrarTodo() }
        coVerify(exactly = 1) {
            productoDao.insertProductos(withArg { lista ->
                lista.shouldHaveSize(2)
                lista[0].nombre.shouldBe("Aros")
                lista[1].nombre.shouldBe("Collar")
            })
        }

        confirmVerified(apiService, productoDao)
    }





    @Test
    fun `registrarUsuario falla si correo ya existe y no inserta`() = runTest {
        val correo = "yaexiste@correo.cl"

        coEvery { userDao.countByEmail(correo.trim()) } returns 1

        val result = repo.registrarUsuario(
            correo = correo,
            password = "pwd",
            sexo = "M",
            edad = 30
        )

        result.isFailure.shouldBeTrue()

        coVerify(exactly = 0) { userDao.insert(any()) }
    }

    // =============== verificarCredenciales =================

    @Test
    fun `verificarCredenciales retorna true cuando validate es mayor que cero`() = runTest {
        val correo = "user@test.cl"
        val password = "pwd"

        coEvery { userDao.validate(correo.trim(), password) } returns 1

        val ok = repo.verificarCredenciales(correo, password)

        ok.shouldBeTrue()
    }

    @Test
    fun `verificarCredenciales retorna false cuando validate es cero`() = runTest {
        val correo = "user@test.cl"
        val password = "pwd"

        coEvery { userDao.validate(correo.trim(), password) } returns 0

        val ok = repo.verificarCredenciales(correo, password)

        ok.shouldBeFalse()
    }
}