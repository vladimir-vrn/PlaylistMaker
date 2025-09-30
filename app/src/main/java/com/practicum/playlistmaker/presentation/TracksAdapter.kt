package com.practicum.playlistmaker.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.Track

class TracksAdapter(
    private val tracks: List<Track>,
    private val onItemClickListener: TracksAdapter.OnItemClickListener
) : RecyclerView.Adapter<TracksViewHolder> () {

    fun interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tracks_view, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}