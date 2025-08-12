package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TracksAdapter() : RecyclerView.Adapter<TracksViewHolder> () {

    var tracks = mutableListOf<Track>()
    var searchHistory: SearchHistory? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tracks_view, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
        if (searchHistory != null) {
            holder.itemView.setOnClickListener {
                searchHistory?.onItemClick(tracks[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}