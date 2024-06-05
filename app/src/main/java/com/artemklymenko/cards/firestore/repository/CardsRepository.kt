package com.artemklymenko.cards.firestore.repository

import com.artemklymenko.cards.db.Words
import com.artemklymenko.cards.firestore.model.Response

interface CardsRepository {

    suspend fun getCardsFromFirestore(userId: String): List<Words>

    suspend fun addCardToFirestore(userId: String, card: Words): Response<String>

    suspend fun deleteCardFromFirestore(userId: String, sid: String?): Response<Boolean>

    suspend fun updateCardInFirestore(userId: String, sid: String?, words: Words): Response<String>

    suspend fun updateCardSid(userId: String, wordsId: Int, newSid: String): Response<Boolean>
}