package com.ksa.infilect.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.*
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import com.ksa.infilect.util.Constants.Companion.PREF_BACK_ONLINE
import com.ksa.infilect.util.Constants.Companion.PREF_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.prefs.Preferences
import javax.inject.Inject

@ActivityRetainedScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PrefKeys {

        val backOnline = preferencesKey<Boolean>(PREF_BACK_ONLINE)
    }


    private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences> =
        context.createDataStore(
            PREF_NAME
        )


    suspend fun saveOnline(backOnline: Boolean) {
        dataStore.edit { pref ->

            pref[PrefKeys.backOnline] = backOnline
        }
    }

    val readBackOnline: Flow<Boolean> = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        val backOnline = preferences[PrefKeys.backOnline] ?: false
        backOnline
    }


}

