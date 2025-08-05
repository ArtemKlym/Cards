package com.artemklymenko.cards.db

import com.artemklymenko.cards.utils.getFakeWordsList

class FakeWordsRepositoryDb: WordDao {

    private val wordsDb: MutableList<Words> = getFakeWordsList().toMutableList()

    override suspend fun insertWords(words: Words) {
        wordsDb.add(words)
    }

    override suspend fun updateWords(words: Words) {
        val index = wordsDb.indexOfFirst {
            it.wordsId == words.wordsId
        }
        wordsDb[index] = words
    }

    override suspend fun deleteWords(words: Words) {
        wordsDb.remove(words)
    }

    override suspend fun getAllWords(): List<Words> {
       return wordsDb
    }

    override suspend fun getWordsByPriority(): List<Words> {
        return wordsDb.sortedBy { it.priority }
    }

    override suspend fun getWords(id: Int): Words {
        return wordsDb.first { it.wordsId == id }
    }

    override suspend fun getUnsyncedWords(): List<Words> {
        return wordsDb.filter { it.sync == 0L }
    }

    override suspend fun updateWordsSid(localId: Int, newSid: String) {
        val index = wordsDb.indexOfFirst { it.wordsId == localId }
        wordsDb[index].sid = newSid
    }

    override suspend fun getOverdueWords(nowMillis: Long, oneMonthMillis: Long): List<Words> {
        return wordsDb.filter { it.lastSeen > 2628000000L }
    }
}