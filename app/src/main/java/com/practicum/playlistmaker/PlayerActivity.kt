package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    var playTime = 30

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
        val track = Gson().fromJson(trackJsonString, Track::class.java)

        val tbPlayer = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.tbPlayer)
        tbPlayer.setNavigationOnClickListener {
            finish()
        }

        val trackImage = findViewById<ImageView>(R.id.trackImage)
        Glide.with(trackImage)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder_512)
            .transform(
                RoundedCorners(
                    dpToPx(8, this)
                )
            )
            .into(trackImage)

        val trackName = findViewById<TextView>(R.id.trackName)
        val artistName = findViewById<TextView>(R.id.artistName)
        val trackPlayTime = findViewById<TextView>(R.id.trackPlayTime)
        val trackTime = findViewById<TextView>(R.id.trackTime)
        val collectionNameTitle = findViewById<TextView>(R.id.collectionNameTitle)
        val collectionName = findViewById<TextView>(R.id.collectionName)
        val releaseDateTitle = findViewById<TextView>(R.id.releaseDateTitle)
        val releaseDate = findViewById<TextView>(R.id.releaseDate)
        val primaryGenreName = findViewById<TextView>(R.id.primaryGenreName)
        val country = findViewById<TextView>(R.id.country)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackPlayTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(playTime * 1000)
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
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

    }
}