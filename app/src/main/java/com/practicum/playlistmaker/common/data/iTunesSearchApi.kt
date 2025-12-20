package com.practicum.playlistmaker.common.data

import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesSearchApi {

    @GET("search?entity=song")
    suspend fun search(
        @Query("term") text: String
    ): TracksSearchResponse
}