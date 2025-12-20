package com.practicum.playlistmaker.common.data

interface StorageClient<T> {
    fun storeData(data: T)
    fun getData(): StorageClientResponse<T>
}