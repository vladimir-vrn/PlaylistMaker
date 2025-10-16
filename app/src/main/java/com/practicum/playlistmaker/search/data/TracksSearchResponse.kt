package com.practicum.playlistmaker.search.data

class TracksSearchResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()