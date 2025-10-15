package com.practicum.playlistmaker.settings.data

interface StorageClient<T> {
    fun storeData(data: T)
    fun getData(): StorageClientResponse<T>
}