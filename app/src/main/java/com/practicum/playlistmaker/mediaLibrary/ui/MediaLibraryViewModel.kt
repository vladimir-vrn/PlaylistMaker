package com.practicum.playlistmaker.mediaLibrary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MediaLibraryViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<MediaLibraryState>(
        MediaLibraryState.FavoritesMissing
    )
    fun observeState(): LiveData<MediaLibraryState> = stateLiveData
}