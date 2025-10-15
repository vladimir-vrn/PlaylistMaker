package com.practicum.playlistmaker.settings.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.lang.reflect.Type

class PrefsStorageClient<T>(
    private val context: Context,
    private val dataKey: String,
    private val type: Type
) : StorageClient<T> {

    private val prefs: SharedPreferences = context.getSharedPreferences(PLAY_LIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun storeData(data: T) {
        prefs.edit().putString(dataKey, gson.toJson(data, type)).apply()
    }

    override fun getData(): StorageClientResponse<T> {
        val dataJson = prefs.getString(dataKey, null)
        if (dataJson == null) {
            return StorageClientResponse<T>(null).apply { resultCode = -1 }
        } else {
            return StorageClientResponse<T>(gson.fromJson(dataJson, type)).apply { resultCode = 200 }
        }
    }

    companion object {
        private const val PLAY_LIST_MAKER_PREFERENCES = "play_list_maker_preferences"
    }

}