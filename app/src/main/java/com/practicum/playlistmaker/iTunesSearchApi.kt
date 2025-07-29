package com.practicum.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesSearchApi {

    @GET("search")
    fun search(
        @Query("term") text: String,
        @Query("entity") entity: String
    ): Call<TracksResponse>
}