package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.common.data.timeFormatMmSs
import com.practicum.playlistmaker.mediaLibrary.domain.FavoriteTracksInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.PlaylistsInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    context: Context,
    private val mediaPlayer: MediaPlayer,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {

    private var stateLiveData: MutableLiveData<PlayerState>
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private var timerUpdateJob: Job? = null

    init {
        if (track.trackId > 0) {

            stateLiveData = MutableLiveData<PlayerState>(
                PlayerState.Content(
                    track,
                    isFavourite = false,
                    STATE_DEFAULT,
                    TIMER_START_TIME,
                    updateTrack = true,
                    updateIsFavourite = true,
                    updateMediaPlayerState = false,
                    updateProgressTime = true,
                    updatePlayLists = true,
                    playLists = emptyList(),
                )
            )

            viewModelScope.launch {
                stateLiveData.postValue(
                    (stateLiveData.value as PlayerState.Content).copy(
                        isFavourite = favoriteTracksInteractor.findTrack(track.trackId).single(),
                        playLists = playlistsInteractor.getPlaylists().single(),
                    )
                )
            }
            preparePlayer(track.previewUrl)

        } else stateLiveData = MutableLiveData<PlayerState>(
            PlayerState.Empty(
                context.getString(R.string.player_open_error_dialog_message)
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        resetTimer()
    }

    fun onTrackPlayClicked() {
        when((stateLiveData.value as PlayerState.Content).mediaPlayerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    fun onTrackAddFavourites() {
        val curState = (stateLiveData.value as PlayerState.Content)
        viewModelScope.launch {
            if (curState.isFavourite)
                favoriteTracksInteractor.deleteTrack(track.trackId)
            else favoriteTracksInteractor.insertTrack(track)
        }
        stateLiveData.postValue(
            curState.copy(
                isFavourite = !curState.isFavourite,
            )
        )
    }

    private fun preparePlayer(previewUrl: String) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            stateLiveData.postValue(
                (stateLiveData.value as PlayerState.Content).copy(
                    mediaPlayerState = STATE_PREPARED,
                    updateMediaPlayerState = true,
                )
            )
        }
        mediaPlayer.setOnCompletionListener {
            stateLiveData.postValue(
                (stateLiveData.value as PlayerState.Content).copy(
                    mediaPlayerState = STATE_PREPARED,
                    updateMediaPlayerState = true,
                )
            )
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        stateLiveData.postValue(
            (stateLiveData.value as PlayerState.Content).copy(
                mediaPlayerState = STATE_PLAYING,
            )
        )
        startTimerUpdate()
    }

    fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        stateLiveData.postValue(
            (stateLiveData.value as PlayerState.Content).copy(
                mediaPlayerState = STATE_PAUSED,
            )
        )
    }

    private fun startTimerUpdate() {
        timerUpdateJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(REFRESH_TRACK_PLAY_TIME)
                stateLiveData.postValue(
                    (stateLiveData.value as PlayerState.Content).copy(
                        progressTime = timeFormatMmSs(mediaPlayer.currentPosition.toLong()),
                    )
                )
            }
        }
    }

    private fun pauseTimer() {
        timerUpdateJob?.cancel()
    }

    private fun resetTimer() {
        timerUpdateJob?.cancel()
        stateLiveData.postValue(
            (stateLiveData.value as PlayerState.Content).copy(
                progressTime = TIMER_START_TIME,
            )
        )
    }

    fun updatePlayLists() {
        viewModelScope.launch {
            stateLiveData.postValue(
                (stateLiveData.value as PlayerState.Content).copy(
                    playLists = playlistsInteractor.getPlaylists().single(),
                )
            )
        }
    }

    fun insertTrackToPlaylist(track: Track, playListId: Int) {
        viewModelScope.launch {
            playlistsInteractor.insertTrack(track, playListId)
            stateLiveData.postValue(
                (stateLiveData.value as PlayerState.Content).copy(
                    playLists = playlistsInteractor.getPlaylists().single(),
                )
            )
        }
    }

    fun getCurTrack(): Track {
        return (stateLiveData.value as PlayerState.Content).track
    }

    companion object {

        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        private const val REFRESH_TRACK_PLAY_TIME = 300L
        private const val TIMER_START_TIME = "00:00"
    }
}