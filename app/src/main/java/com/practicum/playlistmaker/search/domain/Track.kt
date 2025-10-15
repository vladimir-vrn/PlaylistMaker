package com.practicum.playlistmaker.search.domain

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val trackTime: String,
    val artworkUrl100: String,
    val coverArtwork: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
)