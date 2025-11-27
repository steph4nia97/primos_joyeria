package com.primosjoyeria.data.remote.auth



import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class SaintsResponse(
    val date: String,
    val saints: List<String>
)
// 1) Interfaz de la API
interface SaintsApiService {
    @GET("api/saints/today")
    suspend fun getSaintsToday(): SaintsResponse
}


object SaintsApi {
    private const val BASE_URL = "https://catholic-api.herokuapp.com/"

    val service: SaintsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SaintsApiService::class.java)
    }
}