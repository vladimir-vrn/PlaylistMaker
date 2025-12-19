package com.practicum.playlistmaker.common.data

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response

}