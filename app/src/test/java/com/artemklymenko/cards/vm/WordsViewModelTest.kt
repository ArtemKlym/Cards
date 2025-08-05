package com.artemklymenko.cards.vm

import com.artemklymenko.cards.db.FakeWordsRepositoryDb
import com.artemklymenko.cards.db.WordsRepositoryDb
import com.artemklymenko.cards.utils.MainDispatcherRule
import com.artemklymenko.cards.utils.getFakeWord
import com.artemklymenko.cards.utils.getFakeWordsList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WordsViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var fakeWordsDao: FakeWordsRepositoryDb
    private lateinit var fakeWordsRepositoryDb: WordsRepositoryDb
    private lateinit var viewModel: WordsViewModel

    @Before
    fun setup() {
        fakeWordsDao = FakeWordsRepositoryDb()
        fakeWordsRepositoryDb = WordsRepositoryDb(fakeWordsDao)
        viewModel = WordsViewModel(fakeWordsRepositoryDb)
    }

    @Test
    fun `insert a card to database`() = runTest {
        val word = getFakeWord()
        viewModel.insertWords(word)
        val result = viewModel.getWords(word.wordsId)
        assertEquals(word.origin, result.origin)
    }

    @Test
    fun `delete a card from database`() = runTest {
        val words = viewModel.getAllWords()
        val wordToDelete = words[0]

        viewModel.deleteWords(wordToDelete)
        val result = viewModel.getAllWords()

        assertFalse(result.contains(wordToDelete))
    }

    @Test
    fun `update a card from database`() = runTest {
        val words = viewModel.getAllWords()
        val wordToUpdate = words[0]
        wordToUpdate.priority = 10

        viewModel.updateWords(wordToUpdate)
        val result = viewModel.getAllWords()
        assertEquals(10, result[0].priority)
    }

    @Test
    fun `get words by priority from database`() = runTest {
        val initList = getFakeWordsList()
        val result = viewModel.getWordsByPriority()
        assertEquals(initList.sortedBy { it.priority }, result)
    }

    @Test
    fun `get overdue words`() = runTest {
        val fakeWords = getFakeWordsList()
        val result = viewModel.getOverdueWords()
        assertEquals(fakeWords.filter { it.lastSeen > 2628000000L }, result)
    }
}