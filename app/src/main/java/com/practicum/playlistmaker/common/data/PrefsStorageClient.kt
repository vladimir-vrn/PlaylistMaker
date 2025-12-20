package com.practicum.playlistmaker.common.data

import android.content.SharedPreferences
import com.google.gson.Gson
import java.lang.reflect.Type
import androidx.core.content.edit

class PrefsStorageClient<T>(
    private val dataKey: String,
    private val type: Type,
    private val prefs: SharedPreferences,
    private val gson: Gson
) : StorageClient<T> {

    override fun storeData(data: T) {
        prefs.edit {
            putString(
                dataKey,
                gson.toJson(data, type)
            )
        }
    }

    override fun getData(): StorageClientResponse<T> {
        val dataJson = prefs.getString(dataKey, null)
        return if (dataJson == null) {
            StorageClientResponse<T>(null).apply { resultCode = -1 }
        } else {
            StorageClientResponse<T>(
                gson.fromJson(dataJson, type)
            ).apply { resultCode = 200 }
        }
    }
}