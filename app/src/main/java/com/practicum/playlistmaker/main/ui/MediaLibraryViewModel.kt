package com.practicum.playlistmaker.main.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MediaLibraryViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<MediaLibraryActivityState>(
        MediaLibraryActivityState.Content()
    )
    fun observeState(): LiveData<MediaLibraryActivityState> = stateLiveData
}