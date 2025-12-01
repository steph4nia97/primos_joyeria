package com.primosjoyeria.data.remote.auth

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


data class DolarDto(
    val valor: Double,
    val fecha: String
)
interface ApiService {

    // ===== AUTH =====

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegistroRequest): LoginResponse

    // ===== PRODUCTOS =====

    @GET("productos")
    suspend fun getProductos(): List<ProductDto>

    @POST("productos")
    suspend fun crearProducto(
        @Body request: ProductRequest
    ): ProductDto

    @PUT("productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Long,
        @Body request: ProductRequest
    ): ProductDto

    @DELETE("productos/{id}")
    suspend fun eliminarProducto(
        @Path("id") id: Long
    )

    // ===== INDICADORES (DÓLAR) =====
    @GET("indicadores/dolar")
    suspend fun getDolarActual(): DolarDto


    @GET("metal/oro")
    suspend fun getPrecioOro(): MetalPrecioDto

}