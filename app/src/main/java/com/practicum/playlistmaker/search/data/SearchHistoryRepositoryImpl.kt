package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.settings.data.StorageClient

class SearchHistoryRepositoryImpl(
    private val storageClient: StorageClient<List<Track>>
): SearchHistoryRepository {

    override fun load(): List<Track> {

        val response = storageClient.getData()
        return if (response.resultCode == 200) response.data as List<Track>
            else emptyList()
    }

    override fun save(tracks: List<Track>) {

        storageClient.storeData(tracks)

    }
}