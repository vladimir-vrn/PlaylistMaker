package com.practicum.playlistmaker.mediaLibrary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<FavoritesState>(
        FavoritesState.Content()
    )
    fun observeState(): LiveData<FavoritesState> = stateLiveData
}