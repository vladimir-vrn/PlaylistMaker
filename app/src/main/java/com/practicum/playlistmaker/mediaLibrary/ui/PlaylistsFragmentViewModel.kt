package com.practicum.playlistmaker.mediaLibrary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsFragmentViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<PlaylistsFragmentState>(
        PlaylistsFragmentState.Content()
    )
    fun observeState(): LiveData<PlaylistsFragmentState> = stateLiveData
}