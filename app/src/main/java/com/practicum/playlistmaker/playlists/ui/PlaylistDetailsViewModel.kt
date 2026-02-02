package com.practicum.playlistmaker.playlists.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.playlists.domain.PlayList
import com.practicum.playlistmaker.playlists.domain.PlaylistsInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    playList: PlayList,
    private val playlistsInteractor: PlaylistsInteractor,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistDetailsState>()
    fun observeState(): LiveData<PlaylistDetailsState> = stateLiveData

    init {
        viewModelScope.launch {
            stateLiveData.postValue(
                PlaylistDetailsState.Content(
                    playList,
                    playlistsInteractor.getPlaylistTracks(playList.id).single()
                )
            )
        }
    }

    fun updatePlayList(newPlayList: PlayList) {
        stateLiveData.postValue(
            PlaylistDetailsState.Content(
                newPlayList,
                (stateLiveData.value as PlaylistDetailsState.Content).tracks
            )
        )
    }

    fun deletePlayList() {
        viewModelScope.launch {
            playlistsInteractor.deletePlayList(
                (stateLiveData.value as PlaylistDetailsState.Content).playList.id
            )
        }
    }
    fun deleteTrackFromPlayList(track: Track) {

        val playList = (stateLiveData.value as PlaylistDetailsState.Content).playList

        viewModelScope.launch {

            playlistsInteractor.deleteTrack(track.trackId, playList.id)

            stateLiveData.postValue(
                PlaylistDetailsState.Content(
                    playList.copy(
                        numTracks = playList.numTracks - 1,
                        totalTime = playList.totalTime - track.trackTimeMillis
                    ),
                    playlistsInteractor.getPlaylistTracks(playList.id).single()
                )
            )
        }
    }

    fun sharePlayListData(context: Context) {

        val playList = (stateLiveData.value as PlaylistDetailsState.Content).playList
        val tracks = (stateLiveData.value as PlaylistDetailsState.Content).tracks

        var playListData =
            "${playList.name} \n" +
            "${playList.description} \n" +
            PlayListsAdapter.numberEntities(
                playList.numTracks,
                PlayListsAdapter.determineDeclensionTracks(context)
            )
        tracks.forEachIndexed  { index, item ->
            playListData += "\n${(index + 1)}. ${item.artistName} - ${item.trackName}"
        }
        sharingInteractor.shareData(playListData)
    }
}