package com.artemklymenko.cards.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artemklymenko.cards.db.Words
import com.artemklymenko.cards.repository.WordsRepositoryDb
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WordsViewModel @Inject constructor(
    private val repositoryDb: WordsRepositoryDb
): ViewModel(){

    fun insertWords(origin: String, translated: String): Boolean{
        return if(origin.isNotEmpty() && translated.isNotEmpty()){
            viewModelScope.launch(Dispatchers.IO){
                val words = Words(0, origin, translated)
                repositoryDb.insertWords(words)
            }
            true
        }else{
            false
        }
    }

    fun updateWords(id: Int, origin: String, translated: String): Boolean{
        return if(origin.isNotEmpty() && translated.isNotEmpty()){
            viewModelScope.launch(Dispatchers.IO){
                val words = Words(id, origin, translated)
                repositoryDb.updateWords(words)
            }
            true
        }else{
            false
        }
    }

    fun deleteWords(id: Int, origin: String, translated: String): Boolean{
        return if(origin.isNotEmpty() && translated.isNotEmpty()){
            viewModelScope.launch(Dispatchers.IO){
                val words = Words(id, origin, translated)
                repositoryDb.deleteWords(words)
            }
            true
        }else{
            false
        }
    }

    suspend fun getAllWords(): List<Words>{
        return withContext(Dispatchers.IO){
            return@withContext repositoryDb.getAllWords()
        }
    }

    suspend fun getWords(id: Int): Words{
        return withContext(Dispatchers.IO){
            return@withContext repositoryDb.getWords(id)
        }
    }
}