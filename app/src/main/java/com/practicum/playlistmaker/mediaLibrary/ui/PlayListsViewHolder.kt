package com.practicum.playlistmaker.mediaLibrary.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.data.dpToPx
import com.practicum.playlistmaker.mediaLibrary.domain.PlayList
import com.practicum.playlistmaker.databinding.PlaylistsGridViewBinding
import com.practicum.playlistmaker.databinding.PlaylistsViewBinding
import com.practicum.playlistmaker.mediaLibrary.ui.PlayListsAdapter.DeclensionTracks


class PlayListsViewHolder(
    private val binding: ViewBinding,
    private val declensionTracks: DeclensionTracks
): RecyclerView.ViewHolder(binding.root) {

    fun bind(playList: PlayList) {

        if (binding is PlaylistsGridViewBinding) {

            Glide.with(binding.imgPlayListCoverGrid)
                .load(playList.pathCoverFile)
                .placeholder(R.drawable.placeholder_104)
                .transform(
                    CenterCrop(),
                    RoundedCorners(
                        dpToPx(8, binding.imgPlayListCoverGrid.context)
                    )
                )
                .into(binding.imgPlayListCoverGrid)

            binding.txtNameGrid.text = playList.name
            binding.txtNumberTracksGrid.text = tracksCount(playList.trackIDs.size)

        } else if (binding is PlaylistsViewBinding) {

            Glide.with(binding.imgPlayListCoverPlayLists)
                .load(playList.pathCoverFile)
                .placeholder(R.drawable.placeholder_104)
                .transform(
                    CenterCrop(),
                    RoundedCorners(
                        dpToPx(2, binding.imgPlayListCoverPlayLists.context)
                    )
                )
                .into(binding.imgPlayListCoverPlayLists)

            binding.txtName.text = playList.name
            binding.txtNumberTracks.text = tracksCount(playList.trackIDs.size)

        } else return
    }

    private fun tracksCount(count: Int): String {
        return (
                when (count % 100) {
                    in 11..19 -> declensionTracks.genitivePlural
                    else -> when (count % 10) {
                        1 -> declensionTracks.nominativeSingular
                        in 2..4 -> declensionTracks.genitiveSingular
                        else -> declensionTracks.genitivePlural
                    }
                }
            ).format(count)
    }
}

