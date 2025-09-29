package com.practicum.playlistmaker.data

class TracksSearchResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()