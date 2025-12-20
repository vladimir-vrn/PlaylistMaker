package com.practicum.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TracksViewBinding
import com.practicum.playlistmaker.common.data.dpToPx
import com.practicum.playlistmaker.common.domain.Track

class TracksViewHolder(private val binding: TracksViewBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        Glide.with(binding.trackImage)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(
                RoundedCorners(
                    dpToPx(2, binding.trackImage.context)
                )
            )
            .into(binding.trackImage)

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = track.trackTime
    }

    companion object {
        fun from(parent: ViewGroup): TracksViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TracksViewBinding.inflate(inflater, parent, false)
            return TracksViewHolder(binding)
        }
    }
}