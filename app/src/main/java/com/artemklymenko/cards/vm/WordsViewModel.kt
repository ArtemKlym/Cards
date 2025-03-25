package com.artemklymenko.cards.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artemklymenko.cards.db.Words
import com.artemklymenko.cards.db.WordsRepositoryDb
import com.artemklymenko.cards.firestore.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WordsViewModel @Inject constructor(
    private val repositoryDb: WordsRepositoryDb
): ViewModel(){

    fun insertWords(card: Words): Response<Boolean> {
        return try {
            viewModelScope.launch {
                repositoryDb.insertWords(card)
            }
            Response.Success(true)
        }catch (e: Exception){
            Response.Failure(e)
        }
    }

    fun updateWords(card: Words): Response<Boolean>{
        return try {
            viewModelScope.launch {
                repositoryDb.updateWords(card)
            }
            Response.Success(true)
        }catch (e: Exception){
            Response.Failure(e)
        }
    }

    fun deleteWords(card: Words): Response<Boolean> {
        return try {
            viewModelScope.launch {
                repositoryDb.deleteWords(card)
            }
            Response.Success(true)
        }catch (e: Exception){
            Response.Failure(e)
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

    suspend fun getWordsByPriority(): List<Words>{
        return withContext(Dispatchers.IO){
            return@withContext repositoryDb.getWordsByPriority()
        }
    }
}