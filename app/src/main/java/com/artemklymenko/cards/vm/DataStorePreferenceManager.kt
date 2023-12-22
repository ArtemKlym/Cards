package com.artemklymenko.cards.vm

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCoroutinesApi::class)
class DataStorePreferenceManager(context: Context): ViewModel() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(context.packageName)
    private val dataStore = context.dataStore

    private val keyNotice = booleanPreferencesKey("notification")

    var notice
        get() = getBool(keyNotice)
        set(value) = setBool(keyNotice,value)

    private fun getBool(key: Preferences.Key<Boolean>): Boolean{
        return viewModelScope.async {
            runBlocking {
                return@runBlocking dataStore.data.first().toPreferences()[key] ?: false
            }
        }.getCompleted()
    }

    private fun setBool(key: Preferences.Key<Boolean>, value: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }
}