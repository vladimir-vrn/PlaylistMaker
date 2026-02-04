package com.practicum.playlistmaker.playlists.ui

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlists.domain.PlayList
import com.practicum.playlistmaker.playlists.domain.PlaylistsInteractor
import kotlinx.coroutines.launch
import java.io.File

class PlaylistViewModel(
    playList: PlayList?,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {
    private val stateLiveData = MutableLiveData<PlaylistState>(
        PlaylistState.Content(
            playList?.copy() ?:
                PlayList(
                    System.currentTimeMillis(),
                    "",
                    "",
                    "",
                    0,
                    0,
                )
        )
    )
    fun observeState(): LiveData<PlaylistState> = stateLiveData
    val initialPlayList = (stateLiveData.value as PlaylistState.Content).playList

    val isNewPlayList = playList == null

    fun updateData(playList: PlayList) {
        stateLiveData.postValue(
            PlaylistState.Content(playList)
        )
    }

    fun copyCoverFile(
        coverUri: Uri,
        directoryCovers: String,
        context: Context
    ): String? {

        val externalStoragePicturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val subfolderDir = File(externalStoragePicturesDir, directoryCovers)
        if (!subfolderDir.exists()) subfolderDir.mkdir()
        val newCoverFile = File(subfolderDir, coverUri.lastPathSegment ?: "")

        try {
            context.contentResolver.openInputStream(coverUri)?.use { inputStream ->
                newCoverFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            return newCoverFile.absolutePath
        } catch (e: Exception) {
            return null
        }
    }

    fun savePlayList(playList: PlayList) {
        viewModelScope.launch {
            playlistsInteractor.insertPlayList(playList)
        }
    }
}