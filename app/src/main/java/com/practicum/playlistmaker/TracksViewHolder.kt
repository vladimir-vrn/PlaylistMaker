package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TracksViewHolder(trackView: View): RecyclerView.ViewHolder(trackView) {

    private val trackImage: ImageView = trackView.findViewById(R.id.trackImage)
    private val trackName: TextView = trackView.findViewById(R.id.trackName)
    private val artistName: TextView = trackView.findViewById(R.id.artistName)
    private val trackTime: TextView = trackView.findViewById(R.id.trackTime)

    fun bind(track: Track) {
        Glide.with(trackImage)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(
                RoundedCorners(
                    dpToPx(2, trackImage.context)
                )
            )
            .into(trackImage)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = if (track.trackTime == null) "" else SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
    }
}