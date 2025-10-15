package com.practicum.playlistmaker.main.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class MediaLibraryViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<MediaLibraryActivityState>(
        MediaLibraryActivityState.Content()
    )
    fun observeState(): LiveData<MediaLibraryActivityState> = stateLiveData

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MediaLibraryViewModel()
            }
        }
    }
}