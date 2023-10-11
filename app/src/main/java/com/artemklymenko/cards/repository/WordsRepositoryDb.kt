package com.artemklymenko.cards.repository

import com.artemklymenko.cards.db.WordDao
import com.artemklymenko.cards.db.Words
import javax.inject.Inject

class WordsRepositoryDb @Inject constructor(
    private val dao: WordDao
) {
    suspend fun insertWords(words: Words) = dao.insertWords(words)

    suspend fun updateWords(words: Words) = dao.updateWords(words)

    suspend fun deleteWords(words: Words) = dao.deleteWords(words)

    suspend fun getAllWords() = dao.getAllWords()

    suspend fun getWords(wordsId: Int) = dao.getWords(wordsId)
}