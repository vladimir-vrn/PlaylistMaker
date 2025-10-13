package com.practicum.playlistmaker.data

interface NetworkClient {
    fun doRequest(dto: Any): Response

}