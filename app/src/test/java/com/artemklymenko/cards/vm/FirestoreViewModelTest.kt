@file:OptIn(ExperimentalCoroutinesApi::class)

package com.artemklymenko.cards.vm


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.artemklymenko.cards.firestore.model.Response
import com.artemklymenko.cards.firestore.repository.impl.FakeCardsRepositoryImpl
import com.artemklymenko.cards.utils.MainDispatcherRule
import com.artemklymenko.cards.utils.getFakeWord
import com.artemklymenko.cards.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Exception

@OptIn(ExperimentalCoroutinesApi::class)
class FirestoreViewModelTest {

    @get: Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: FirestoreViewModel
    private lateinit var fakeRepository: FakeCardsRepositoryImpl

    companion object {
        private const val USER_ID1 = "1"
        private const val NEW_SID = "kbkmdskmf"
        private const val SUCCESS_RESULT = true
    }

    @Before
    fun setup() {
        fakeRepository = FakeCardsRepositoryImpl()
        runBlocking {
            fakeRepository.getCardsFromFirestore(USER_ID1)
        }
        viewModel = FirestoreViewModel(fakeRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testAddCardToFirestore() = runTest {
        viewModel.addCardToFirestore(userId = USER_ID1, card = getFakeWord())
        advanceUntilIdle()
        val result = viewModel.addCardResult.getOrAwaitValue()
        assertEquals(NEW_SID, (result as Response.Success).data)
    }

    @Test
    fun testDeleteCardFromFirestore() = runTest {
        viewModel.deleteCardFromFirestore(userId = USER_ID1, NEW_SID)
        advanceUntilIdle()
        val result = viewModel.deleteCardResult.getOrAwaitValue()
        assertEquals(SUCCESS_RESULT, (result as Response.Success).data)
    }

    @Test
    fun testUpdateCardInFirestore() = runTest {
        val newCard = getFakeWord()
        viewModel.updateCardFromFirestore(userId = USER_ID1, sid = newCard.sid, words = newCard)
        advanceUntilIdle()
        val result = viewModel.updateResultCard.getOrAwaitValue()
        assertEquals(NEW_SID, (result as Response.Success).data)
    }

    @Test
    fun testFailureUpdateCardInFirestore() = runTest {
        viewModel.updateCardFromFirestore(userId = "4", sid = null, words = getFakeWord())
        advanceUntilIdle()
        val result = viewModel.updateResultCard.getOrAwaitValue()
        assertTrue(result is Response.Failure)
        assertEquals("Card not Found", (result as Response.Failure).e?.message)
    }
}