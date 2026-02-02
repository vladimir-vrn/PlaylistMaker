package com.practicum.playlistmaker.playlists.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlists.domain.PlayList
import com.practicum.playlistmaker.databinding.PlaylistsGridViewBinding
import com.practicum.playlistmaker.databinding.PlaylistsViewBinding

class PlayListsAdapter(
    val layoutOption: Int,
    val declensionTracks: DeclensionEntities,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<PlayListsViewHolder> () {

    data class DeclensionEntities(
        val nominativeSingular: String,
        val genitiveSingular: String,
        val genitivePlural: String,
    )

    var playLists = mutableListOf<PlayList>()
    fun interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PlayListsViewHolder(
            if (layoutOption == LAYOUT_OPTION_LINEAR)
                PlaylistsViewBinding.inflate(inflater, parent, false)
            else PlaylistsGridViewBinding.inflate(inflater, parent, false),
            declensionTracks
        )
    }

    override fun onBindViewHolder(holder: PlayListsViewHolder, position: Int) {
        holder.bind(playLists[position])
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return playLists.size
    }

    companion object {
        const val LAYOUT_OPTION_LINEAR = 0
        const val LAYOUT_OPTION_GRID = 1

        fun determineDeclensionTracks(context: Context): DeclensionEntities {
            return DeclensionEntities(
                context.getString(R.string.playlists_track_nominative_singular),
                context.getString(R.string.playlists_track_genitive_singular),
                context.getString(R.string.playlists_track_genitive_plural)
            )
        }

        fun determineDeclensionMinutes(context: Context): DeclensionEntities {
            return DeclensionEntities(
                context.getString(R.string.playlists_minute_nominative_singular),
                context.getString(R.string.playlists_minute_genitive_singular),
                context.getString(R.string.playlists_minute_genitive_plural)
            )
        }

        fun numberEntities(count: Int, declensionEntities: DeclensionEntities): String {
            return (when (count % 100) {
                    in 11..19 -> declensionEntities.genitivePlural
                    else -> when (count % 10) {
                        1 -> declensionEntities.nominativeSingular
                        in 2..4 -> declensionEntities.genitiveSingular
                        else -> declensionEntities.genitivePlural
                    }
                }).format(count)
        }
    }

}