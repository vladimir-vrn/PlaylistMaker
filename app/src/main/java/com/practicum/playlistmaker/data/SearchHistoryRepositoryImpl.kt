package com.practicum.playlistmaker.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.SearchHistoryRepository
import com.practicum.playlistmaker.domain.Track
import androidx.core.content.edit
import com.practicum.playlistmaker.timeFormatMmSs

class SearchHistoryRepositoryImpl: SearchHistoryRepository {

    private var playListPrefs: SharedPreferences? = null
    private val gson = Gson()

    override fun load(context: Context): List<Track> {
        val jsonString = getPlayListPrefs(context).getString(SEARCH_HISTORY_KEY, null) ?: return listOf<Track>()
        val savedTracks:List<TrackDto> = gson.fromJson(jsonString, object : TypeToken<ArrayList<TrackDto>>() {}.type)
        return savedTracks.map {
            Track(
                it.trackId,
                it.trackName,
                it.artistName,
                it.trackTimeMillis,
                timeFormatMmSs(it.trackTimeMillis),
                it.artworkUrl100,
                it.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"),
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        }
    }

    override fun save(tracks: List<Track>, context: Context) {
        val jsonString = gson.toJson(tracks.map {
            TrackDto(
                it.trackId,
                it.trackName,
                it.artistName,
                it.trackTimeMillis,
                it.artworkUrl100,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        })
        getPlayListPrefs(context).edit {
            putString(SEARCH_HISTORY_KEY, jsonString)
        }
    }

    private fun getPlayListPrefs(context: Context): SharedPreferences {
        if (playListPrefs == null) {
            playListPrefs = context.getSharedPreferences(PLAY_LIST_MAKER_PREFERENCES, MODE_PRIVATE)
        }
        return playListPrefs!!
    }

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history_tracks"
        private const val PLAY_LIST_MAKER_PREFERENCES = "play_list_maker_preferences"
    }
}