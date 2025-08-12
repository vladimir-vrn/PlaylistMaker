package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory (
    val playListPrefs: SharedPreferences
) {
    private val tracks = getSavedTracks().toMutableList()

    fun getSavedTracks(): List<Track> {
        val jsonString = playListPrefs.getString(SEARCH_HISTORY_KEY, null) ?: return listOf<Track>()
        val savedTracks:List<Track> = Gson().fromJson(jsonString, object : TypeToken<ArrayList<Track>>() {}.type)
        return savedTracks
    }

    fun onSaveTracks() {
        val jsonString = Gson().toJson(tracks.toList())
        playListPrefs.edit()
            .putString(SEARCH_HISTORY_KEY, jsonString)
            .apply()
    }
    fun getTracks(): List<Track> {
        return tracks.toList()
    }

    fun clearTracks() {
        return tracks.clear()
    }

    fun onItemClick(track: Track) {
        val trackPosition = tracks.indexOf(track)
        if (trackPosition == -1) {
            if (tracks.size == DEPTH_SEARCH_HISTORY) tracks.removeAt(DEPTH_SEARCH_HISTORY - 1)
        } else {
            tracks.removeAt(trackPosition)
        }
        tracks.add(0, track)
    }

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history_tracks"
        private const val DEPTH_SEARCH_HISTORY = 10
    }

}