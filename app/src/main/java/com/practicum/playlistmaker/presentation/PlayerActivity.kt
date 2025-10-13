package com.practicum.playlistmaker.presentation

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.dpToPx
import com.practicum.playlistmaker.timeFormatMmSs

class PlayerActivity : AppCompatActivity() {
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private lateinit var trackPlay: ImageView
    private lateinit var trackPlayTime: TextView
    private lateinit var track: Track
    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            trackPlayTime.text = timeFormatMmSs(mediaPlayer.currentPosition.toLong())
            handler.postDelayed(
                this,
                REFRESH_TRACK_PLAY_TIME,
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val trackJsonString = intent.getStringExtra("track")
        if (trackJsonString == null) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.player_open_error_dialog_title))
                .setMessage(getString(R.string.player_open_error_dialog_message))
                .setPositiveButton(getString(R.string.player_open_error_dialog_positive_btn)) { dialog, which -> finish() }
            val dialog = builder.create()
            dialog.show()
            return
        }
        track = Gson().fromJson(trackJsonString, Track::class.java)

        val tbPlayer = findViewById<MaterialToolbar>(R.id.tbPlayer)
        tbPlayer.setNavigationOnClickListener {
            finish()
        }

        val trackImage = findViewById<ImageView>(R.id.trackImage)
        Glide.with(trackImage)
            .load(track.coverArtwork)
            .placeholder(R.drawable.placeholder_512)
            .transform(
                RoundedCorners(
                    dpToPx(8, this)
                )
            )
            .into(trackImage)

        val trackName = findViewById<TextView>(R.id.trackName)
        val artistName = findViewById<TextView>(R.id.artistName)
        val trackTime = findViewById<TextView>(R.id.trackTime)
        val collectionNameTitle = findViewById<TextView>(R.id.collectionNameTitle)
        val collectionName = findViewById<TextView>(R.id.collectionName)
        val releaseDateTitle = findViewById<TextView>(R.id.releaseDateTitle)
        val releaseDate = findViewById<TextView>(R.id.releaseDate)
        val primaryGenreName = findViewById<TextView>(R.id.primaryGenreName)
        val country = findViewById<TextView>(R.id.country)

        trackPlay = findViewById(R.id.trackPlay)
        trackPlay.setOnClickListener { playbackControl() }
        trackPlayTime = findViewById(R.id.trackPlayTime)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime
        if (track.collectionName.isNullOrEmpty()) {
            collectionNameTitle.visibility = View.GONE
            collectionName.visibility = View.GONE
        } else {
            collectionName.text = track.collectionName
        }
        if (track.releaseDate.isNullOrEmpty()) {
            releaseDateTitle.visibility = View.GONE
            releaseDate.visibility = View.GONE
        } else {
            releaseDate.text = track.releaseDate.take(4)
        }
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        preparePlayer()

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(refreshRunnable)
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            trackPlay.setImageResource(R.drawable.track_play)
            handler.removeCallbacks(refreshRunnable)
            trackPlayTime.text = getString(R.string.player_track_play_time)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        trackPlay.setImageResource(R.drawable.track_pause)
        handler.postDelayed(refreshRunnable, REFRESH_TRACK_PLAY_TIME)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        trackPlay.setImageResource(R.drawable.track_play)
        handler.removeCallbacks(refreshRunnable)
    }

    private fun playbackControl() {

        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_TRACK_PLAY_TIME = 500L
    }
}