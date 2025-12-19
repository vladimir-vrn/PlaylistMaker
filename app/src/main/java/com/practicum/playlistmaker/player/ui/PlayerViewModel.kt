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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    track: Track?,
    context: Context,
    private val mediaPlayer: MediaPlayer
) : ViewModel() {

    private var stateLiveData: MutableLiveData<PlayerState>
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData(TIMER_START_TIME)
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private var timerUpdateJob: Job? = null

    init {
        stateLiveData = MutableLiveData<PlayerState>(
            when {
                track == null -> PlayerState.Error(
                    context.getString(R.string.player_open_error_dialog_message)
                )
                track.trackId > 0 -> {
                    preparePlayer(track.previewUrl)
                    PlayerState.Content(track)
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
        when(playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun preparePlayer(previewUrl: String) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.postValue(STATE_PREPARED)
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(STATE_PLAYING)
        startTimerUpdate()
    }

    fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        playerStateLiveData.postValue(STATE_PAUSED)
    }

    private fun startTimerUpdate() {
        timerUpdateJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(REFRESH_TRACK_PLAY_TIME)
                progressTimeLiveData.postValue(timeFormatMmSs(mediaPlayer.currentPosition.toLong()))
            }
        }
    }

    private fun pauseTimer() {
        timerUpdateJob?.cancel()
    }

    private fun resetTimer() {
        timerUpdateJob?.cancel()
        progressTimeLiveData.postValue(TIMER_START_TIME)
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