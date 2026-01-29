package com.practicum.playlistmaker.mediaLibrary.ui

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.PlayList
import com.practicum.playlistmaker.mediaLibrary.domain.PlaylistsInteractor
import kotlinx.coroutines.launch
import java.io.File

class PlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {
    private val stateLiveData = MutableLiveData<PlaylistState>(
        PlaylistState.Content()
    )
    fun observeState(): LiveData<PlaylistState> = stateLiveData

    fun displayImage(uri: String) {
        stateLiveData.postValue(
            (stateLiveData.value as PlaylistState.Content).copy(
                coverUri = uri
            )
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

    fun updateData(data: PlaylistState.Content) {
        stateLiveData.postValue(
            (stateLiveData.value as PlaylistState.Content).copy(
                name = data.name,
                description = data.description
            )
        )
    }

    fun createPlayList(playList: PlayList) {
        viewModelScope.launch {
            playlistsInteractor.insertPlayList(playList)
        }
    }
}