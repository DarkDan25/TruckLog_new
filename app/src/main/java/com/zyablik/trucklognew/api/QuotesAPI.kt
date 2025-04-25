package com.zyablik.trucklognew.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Объект аниме-цитата (хранит в себе название произведения, автора цитаты и саму цитату)
data class AnimeQuote(
    val show: String,
    val character: String,
    val quote: String
)

// Интерфейс для получения анинме-цитаты (это из практической по разработке мобильных приложений)
interface AnimeQuotesApiService {
    @GET("api/quotes")
    suspend fun getQuotesByAnime(@Query("show") show: String): List<AnimeQuote>
}

// Настройка связи с внешним API
object RetrofitClient {
    private const val BASE_URL = "https://yurippe.vercel.app/"

    val instance: AnimeQuotesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnimeQuotesApiService::class.java)
    }
}