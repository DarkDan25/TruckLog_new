package com.zyablik.trucklognew.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class AnimeQuote(
    val anime: String,
    val character: String,
    val quote: String
)


interface AnimeQuotesApiService {
    @GET("api/quotes")
    suspend fun getQuotesByAnime(@Query("show") show: String): List<AnimeQuote>
}


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