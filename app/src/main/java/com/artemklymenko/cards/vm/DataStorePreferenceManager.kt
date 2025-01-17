package com.artemklymenko.cards.vm

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar


@OptIn(ExperimentalCoroutinesApi::class)
class DataStorePreferenceManager(context: Context): ViewModel() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(context.packageName)
    private val dataStore = context.dataStore
    private var isInitialized = false
    private val _consecutiveDaysFlow: MutableStateFlow<Int> = MutableStateFlow(0)
    val consecutiveDaysFlow: Flow<Int> = _consecutiveDaysFlow

    private val keyNotice = booleanPreferencesKey("notification")
    private val keyLastOpenedDay = intPreferencesKey("last_opened_day")
    private val keyLastOpenedMonth = intPreferencesKey("last_opened_month")
    private val keyLastOpenedYear = intPreferencesKey("last_opened_year")
    private val keyIncrement = intPreferencesKey("count_of_days")
    private val keyLastSync = longPreferencesKey("last_sync")
    private val keyTargetLang = stringPreferencesKey("target_lang")
    private val keySourceLang = stringPreferencesKey("source_lang")

    init {
        initializeIfNeeded()
    }

    private fun initializeIfNeeded() {
        synchronized(this) {
            if (!isInitialized) {
                viewModelScope.launch {
                    runBlocking {
                        _consecutiveDaysFlow.value = checkAndUpdateConsecutiveDays()
                    }
                }
                isInitialized = true
            }
        }
    }

    var lastSyncTime: Long
        get() = runBlocking {getLong(keyLastSync) ?: 0L}
        set(value) = setLong(keyLastSync, value)
    var notice
        get() = getBool(keyNotice)
        set(value) = setBool(keyNotice,value)
    var sourceLang
        get() = getSource(keySourceLang)
        set(value) = setSource(keySourceLang, value)
    var targetLang
        get() = getTarget(keyTargetLang)
        set(value) = setTarget(keyTargetLang, value)

    companion object {
        @Volatile
        private var instance: DataStorePreferenceManager? = null

        fun getInstance(context: Context): DataStorePreferenceManager {
            return instance ?: synchronized(this) {
                instance ?: DataStorePreferenceManager(context).also { instance = it }
            }
        }
    }

    private suspend fun getLong(key: Preferences.Key<Long>): Long? {
        return dataStore.data.first()[key]
    }

    private fun setLong(key: Preferences.Key<Long>, value: Long) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    private fun getBool(key: Preferences.Key<Boolean>): Boolean {
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

    private fun setTarget(keyTargetLang: Preferences.Key<String>, value: String) {
        viewModelScope.launch {
            dataStore.edit { preference ->
                preference[keyTargetLang] = value
            }
        }
    }

    private fun getTarget(keyTargetLang: Preferences.Key<String>): String {
        return viewModelScope.async {
            runBlocking {
                return@runBlocking dataStore.data.first().toPreferences()[keyTargetLang] ?: "uk"
            }
        }.getCompleted()
    }

    private fun setSource(keySourceLang: Preferences.Key<String>, value: String) {
        viewModelScope.launch {
            dataStore.edit { preference ->
                preference[keySourceLang] = value
            }
        }
    }

    private fun getSource(keySourceLang: Preferences.Key<String>): String {
        return viewModelScope.async {
            runBlocking {
                return@runBlocking dataStore.data.first().toPreferences()[keySourceLang] ?: "en"
            }
        }.getCompleted()
    }

    private suspend fun checkAndUpdateConsecutiveDays(): Int {
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        var counterOfConsecutiveDays = dataStore.data.first()[keyIncrement] ?: 0
        val lastOpenedDay = dataStore.data.first()[keyLastOpenedDay] ?: 0
        val lastOpenedMonth = dataStore.data.first()[keyLastOpenedMonth] ?: 0
        val lastOpenedYear = dataStore.data.first()[keyLastOpenedYear] ?: 0

        if (currentDay - 1 == lastOpenedDay ||
            currentDay == 1 && (currentMonth > lastOpenedMonth) ||
            currentDay == 1 && (currentYear > lastOpenedYear)) {
            Log.d(
                "DataStore",
                "DataStore if: currentDay = $currentDay lastOpened day = $lastOpenedDay"
            )
            counterOfConsecutiveDays++
        }else if(currentDay == lastOpenedDay){
            Log.d(
                "DataStore",
                "DataStore else if: currentDay = $currentDay lastOpened day = $lastOpenedDay"
            )
        }else{
            counterOfConsecutiveDays = 0
        }

        dataStore.edit { preferences ->
            preferences[keyIncrement] = counterOfConsecutiveDays
            preferences[keyLastOpenedDay] = currentDay
            preferences[keyLastOpenedMonth] = currentMonth
            preferences[keyLastOpenedYear] = currentYear
        }
        return counterOfConsecutiveDays
    }
}