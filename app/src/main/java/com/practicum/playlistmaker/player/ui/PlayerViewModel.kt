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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    context: Context,
    private val mediaPlayer: MediaPlayer,
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private var stateLiveData: MutableLiveData<PlayerState>
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private var timerUpdateJob: Job? = null

    init {
        stateLiveData = MutableLiveData<PlayerState>(
            when {
                track.trackId > 0 -> {
                    preparePlayer(track.previewUrl)
                    PlayerState.Content(
                        track,
                        isFavourite = track.isFavorite,
                        STATE_DEFAULT,
                        TIMER_START_TIME,
                        updateTrack = true,
                        updateIsFavourite = true,
                        updateMediaPlayerState = false,
                        updateProgressTime = true,
                    )
                }
                else -> PlayerState.Empty(
                    context.getString(R.string.player_open_error_dialog_message)
                )
            }
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
                updateTrack = false,
                updateIsFavourite = true,
                updateMediaPlayerState = false,
                updateProgressTime = false,
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
                    updateTrack = false,
                    updateIsFavourite = false,
                    updateMediaPlayerState = true,
                    updateProgressTime = false,
                )
            )
        }
        mediaPlayer.setOnCompletionListener {
            stateLiveData.postValue(
                (stateLiveData.value as PlayerState.Content).copy(
                    mediaPlayerState = STATE_PREPARED,
                    updateTrack = false,
                    updateIsFavourite = false,
                    updateMediaPlayerState = true,
                    updateProgressTime = false,
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
                updateTrack = false,
                updateIsFavourite = false,
                updateMediaPlayerState = true,
                updateProgressTime = false,
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
                updateTrack = false,
                updateIsFavourite = false,
                updateMediaPlayerState = true,
                updateProgressTime = false,
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
                        updateTrack = false,
                        updateIsFavourite = false,
                        updateMediaPlayerState = false,
                        updateProgressTime = true,
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
                updateTrack = false,
                updateIsFavourite = false,
                updateMediaPlayerState = false,
                updateProgressTime = true,
            )
        )
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