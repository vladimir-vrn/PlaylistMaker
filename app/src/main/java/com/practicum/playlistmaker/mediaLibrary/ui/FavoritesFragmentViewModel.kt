package com.practicum.playlistmaker.mediaLibrary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesFragmentViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<FavoritesFragmentState>(
        FavoritesFragmentState.Content()
    )
    fun observeState(): LiveData<FavoritesFragmentState> = stateLiveData
}