package com.artemklymenko.cards.firestore.repository.impl

import com.artemklymenko.cards.db.Words
import com.artemklymenko.cards.firestore.model.Response
import com.artemklymenko.cards.firestore.repository.CardsRepository
import com.artemklymenko.cards.utils.getUsersWordsList

class FakeCardsRepositoryImpl: CardsRepository {

    private var usersCards = mutableMapOf<String,MutableList<Words>>()

    companion object {
        private const val NEW_SID = "kbkmdskmf"
        private const val SUCCESS_RESULT = true
        private const val FAILURE_RESULT = "Something went wrong"
    }

    override suspend fun getCardsFromFirestore(userId: String): List<Words> {
        val usersList = getUsersWordsList()
        for((key,value) in usersList) {
            usersCards.getOrPut(key) { mutableListOf<Words>() }.addAll(value)
        }
        return usersCards[userId]!!
    }

    override suspend fun addCardToFirestore(userId: String, card: Words): Response<String> {
        usersCards[userId]?.add(card)
        return if (usersCards[userId]!!.contains(card)) Response.Success(data = NEW_SID) else Response.Failure(
            Exception(FAILURE_RESULT)
        )
    }

    override suspend fun deleteCardFromFirestore(userId: String, sid: String?): Response<Boolean> {
        usersCards[userId]?.removeIf { it.sid == sid }
        return if (usersCards[userId]!!.none { it.sid == sid }) Response.Success(data = SUCCESS_RESULT) else Response.Failure(
            Exception(FAILURE_RESULT)
        )
    }

    override suspend fun updateCardInFirestore(
        userId: String,
        sid: String?,
        words: Words
    ): Response<String> {
        val list = usersCards[userId]
        if (list != null && sid != null) {
            val index = list.indexOfFirst { it.sid == sid }
            if (index != -1) {
                list[index] = words
                return Response.Success(NEW_SID)
            }
        }
        return Response.Failure(Exception("Card not Found"))
    }
}