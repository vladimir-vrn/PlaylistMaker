package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class TracksAdapter(val context: Context) : RecyclerView.Adapter<TracksViewHolder> () {

    var tracks = mutableListOf<Track>()
    var searchHistory: SearchHistory? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tracks_view, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            if (searchHistory != null) searchHistory?.onItemClick(tracks[position])
            openPlayer(tracks[position])
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    private fun openPlayer(track: Track) {

        val intentPlayerActivity = Intent(context, PlayerActivity::class.java)
        val trackJsonString = Gson().toJson(track)
        intentPlayerActivity.putExtra("track", trackJsonString)
        context.startActivity(intentPlayerActivity)
    }

}