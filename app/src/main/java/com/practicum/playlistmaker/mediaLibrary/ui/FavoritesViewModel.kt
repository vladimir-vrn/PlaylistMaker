package com.practicum.playlistmaker.mediaLibrary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.FavoriteTracksInteractor
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoritesState>(
        FavoritesState.Content()
    )
    fun observeState(): LiveData<FavoritesState> = stateLiveData

    fun updateFavoriteTracks() {
        viewModelScope.launch {
            favoriteTracksInteractor
                .getFavoriteTracks()
                .collect { tracks ->
                    stateLiveData.postValue(FavoritesState.Content(tracks))
                }
        }
    }
}