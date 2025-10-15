package com.practicum.playlistmaker.settings.data

import com.practicum.playlistmaker.search.data.Response

class StorageClientResponse<T>(
    val data: T?
) : Response()