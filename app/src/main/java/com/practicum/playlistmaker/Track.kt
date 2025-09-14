package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
) {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}