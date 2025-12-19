package com.practicum.playlistmaker.mediaLibrary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MediaLibraryViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<MediaLibraryState>(
        MediaLibraryState.Content(0)
    )
    fun observeState(): LiveData<MediaLibraryState> = stateLiveData

    fun setCurrentItem(currentItem: Int) {
        stateLiveData.postValue(MediaLibraryState.Content(currentItem))
    }
}