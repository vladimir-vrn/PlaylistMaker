package com.practicum.playlistmaker.player.ui

import android.app.Application
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.utils.timeFormatMmSs

class PlayerViewModel(startState: PlayerActivityState) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerActivityState>(startState)
    fun observeState(): LiveData<PlayerActivityState> = stateLiveData

    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData(TIMER_START_TIME)
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private val mediaPlayer = MediaPlayer()

    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = Runnable {
        if (playerStateLiveData.value == STATE_PLAYING) {
            startTimerUpdate()
        }
    }

    init {
        if (startState is PlayerActivityState.Content) {
            preparePlayer(startState.track.previewUrl)
        }
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
        progressTimeLiveData.postValue(timeFormatMmSs(mediaPlayer.currentPosition.toLong()))
        handler.postDelayed(timerRunnable, REFRESH_TRACK_PLAY_TIME)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        progressTimeLiveData.postValue(TIMER_START_TIME)
    }

    companion object {
        fun getFactory(trackJsonString: String?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = this[APPLICATION_KEY] as Application
                if (trackJsonString.isNullOrEmpty()) {
                    PlayerViewModel(
                        PlayerActivityState.Error(context.getString(R.string.player_open_error_dialog_message))
                    )
                } else {
                    try {
                        val track = Gson().fromJson(trackJsonString, Track::class.java)
                        if (track.trackId > 0) {
                            PlayerViewModel(
                                PlayerActivityState.Content(track)
                            )
                        } else {
                            PlayerViewModel(
                                PlayerActivityState.Empty(context.getString(R.string.player_open_error_dialog_message))
                            )
                        }
                    } catch (e: Exception) {
                        PlayerViewModel(
                            PlayerActivityState.Error(context.getString(R.string.player_open_error_dialog_message))
                        )
                    }
                }
            }
        }

        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        private const val REFRESH_TRACK_PLAY_TIME = 500L
        private const val TIMER_START_TIME = "00:00"
    }
}