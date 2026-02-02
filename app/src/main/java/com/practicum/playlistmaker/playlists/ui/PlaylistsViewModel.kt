package com.practicum.playlistmaker.playlists.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlists.domain.PlaylistsInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {
    private val stateLiveData = MutableLiveData<PlaylistsState>(
        PlaylistsState.Content()
    )
    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    fun getPlayLists() {

        viewModelScope.launch {
            playlistsInteractor.getPlaylists()
                .collect { playLists ->
                    stateLiveData.postValue(PlaylistsState.Content(playLists))
                }
        }
    }
}