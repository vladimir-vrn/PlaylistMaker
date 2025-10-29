package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.utils.timeFormatMmSs
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlayerViewModel(
    track: Track?,
    context: Context,
) : ViewModel(), KoinComponent {

    private var stateLiveData: MutableLiveData<PlayerActivityState>
    fun observeState(): LiveData<PlayerActivityState> = stateLiveData

    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData(TIMER_START_TIME)
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private val mediaPlayer by inject<MediaPlayer>()

    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = Runnable {
        if (playerStateLiveData.value == STATE_PLAYING) {
            startTimerUpdate()
        }
    }

    init {
        stateLiveData = MutableLiveData<PlayerActivityState>(
            when {
                track == null -> PlayerActivityState.Error(
                    context.getString(R.string.player_open_error_dialog_message)
                )
                track.trackId > 0 -> {
                    preparePlayer(track.previewUrl)
                    PlayerActivityState.Content(track)
                }
                else -> PlayerActivityState.Empty(
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

        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        private const val REFRESH_TRACK_PLAY_TIME = 500L
        private const val TIMER_START_TIME = "00:00"
    }
}