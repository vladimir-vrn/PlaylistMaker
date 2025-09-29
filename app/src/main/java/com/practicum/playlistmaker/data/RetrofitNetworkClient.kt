package com.practicum.playlistmaker.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesSearch = retrofit.create<iTunesSearchApi>()

    override fun doRequest(dto: Any): Response {

        val errorResp400 = Response().apply { resultCode = 400 }

        if (dto is TracksSearchRequest) {
            try {
                val resp = iTunesSearch.search(dto.expression).execute()
                if (resp.isSuccessful) {
                    val body = resp.body() ?: Response()
                    return body.apply { resultCode = resp.code() }
                } else {
                    return errorResp400
                }
            } catch (e: Exception) {
                return errorResp400
            }
        } else {
            return errorResp400
        }
    }

    companion object {
        private const val BASE_URL = "https://itunes.apple.com"
    }
}