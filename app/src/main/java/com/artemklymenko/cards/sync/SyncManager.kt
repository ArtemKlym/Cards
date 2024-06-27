package com.artemklymenko.cards.sync

import android.content.Context
import com.artemklymenko.cards.db.Words
import com.artemklymenko.cards.db.WordsRepositoryDb
import com.artemklymenko.cards.firestore.model.Response
import com.artemklymenko.cards.firestore.repository.CardsRepository
import com.artemklymenko.cards.vm.DataStorePreferenceManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SyncManager @Inject constructor(
    private val firestoreRepository: CardsRepository,
    private val wordsRepository: WordsRepositoryDb,
    private val appContext: Context,
) {
    suspend fun synchronizeData(userId: String): Boolean {
        val dataStorePreference = DataStorePreferenceManager.getInstance(appContext)
        val currentTime = System.currentTimeMillis()
        val lastSyncTime = dataStorePreference.lastSyncTime
        val oneDayInMillis = TimeUnit.DAYS.toMillis(1)
        return if (currentTime - lastSyncTime >= oneDayInMillis) {
            val localCards = wordsRepository.getAllWords()
            val firestoreCards = getFirestoreCardsAsCardsList(userId)
            // Synchronize data
            synchronizeWithFirestore(localCards, firestoreCards, userId)
            dataStorePreference.lastSyncTime = currentTime
            true
        } else {
            false
        }
    }

    private suspend fun synchronizeWithFirestore(
        localCards: List<Words>,
        firestoreCards: List<Words>,
        userId: String
    ) {
        if (firestoreCards.isEmpty() && localCards.isNotEmpty()) {
            // Firestore is empty, upload all local data to Firestore
            localCards.forEach { card ->
                val newSidResponse = firestoreRepository.addCardToFirestore(userId, card)
                if (newSidResponse is Response.Success) {
                    wordsRepository.updateWordsSid(card.wordsId, newSidResponse.data)
                }
            }
        } else {
            // Check for new or updated data in Firestore
            firestoreCards.forEach { firestoreCard ->
                val localCard = localCards.find { it.sid == firestoreCard.sid }
                if (localCard == null || localCard.sync < firestoreCard.sync) {
                    wordsRepository.insertWords(firestoreCard)
                }
            }

            // After inserting new data into Firestore, update local objects with server IDs (sids)
            val unsyncedCards = wordsRepository.getUnsyncedWords()
            unsyncedCards.forEach { card ->
                val newSidResponse = firestoreRepository.addCardToFirestore(userId, card)
                if (newSidResponse is Response.Success) {
                    wordsRepository.updateWordsSid(card.wordsId, newSidResponse.data)
                }
            }
        }
    }

    private suspend fun getFirestoreCardsAsCardsList(userId: String): List<Words> {
        return firestoreRepository.getCardsFromFirestore(userId)
    }
}