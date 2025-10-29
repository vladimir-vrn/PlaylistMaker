package com.practicum.playlistmaker.main.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<MainActivityState>(
        MainActivityState.Content()
    )
    fun observeState(): LiveData<MainActivityState> = stateLiveData
}