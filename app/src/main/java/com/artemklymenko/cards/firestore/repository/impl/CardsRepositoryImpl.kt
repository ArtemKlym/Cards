package com.artemklymenko.cards.firestore.repository.impl

import com.artemklymenko.cards.db.Words
import com.artemklymenko.cards.firestore.model.Response
import com.artemklymenko.cards.firestore.repository.CardsRepository
import com.artemklymenko.cards.firestore.utils.await
import com.artemklymenko.cards.utils.Constants.CARDS
import com.artemklymenko.cards.utils.Constants.USERS
import com.google.firebase.firestore.CollectionReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardsRepositoryImpl @Inject constructor(
    private val cardsRef: CollectionReference
) : CardsRepository {
    override suspend fun getCardsFromFirestore(userId: String): List<Words> {
        return try{
            val cardCollectionRef = cardsRef.firestore
                .collection(USERS)
                .document(userId)
                .collection(CARDS)

            val querySnapshot = cardCollectionRef.get().await()
            val cardList = querySnapshot.documents.mapNotNull {document ->
                document.toObject(Words::class.java)?.apply {
                    sid = document.id
                }
            }
            cardList
        }catch (e: Exception){
            emptyList()
        }
    }

    override suspend fun addCardToFirestore(userId: String, card: Words): Response<String> {
        try {
            val cardsCollectionRef = cardsRef.firestore
                .collection(USERS)
                .document(userId)
                .collection(CARDS)

            val cardData = hashMapOf(
                "wordsId" to card.wordsId,
                "origin" to card.origin,
                "translated" to card.translated,
                "sourceLangCode" to card.sourceLangCode,
                "targetLangCode" to card.targetLangCode,
                "sync" to card.sync
            )

            val documentReference = cardsCollectionRef.add(cardData).await()

            val newSid = documentReference.id

            val cardWithSid = card.copy(sid = newSid)

            cardsCollectionRef.document(newSid).set(cardWithSid).await()

            return Response.Success(newSid)
        } catch (e: Exception) {
            return Response.Failure(e)
        }
    }

    override suspend fun deleteCardFromFirestore(
        userId: String,
        sid: String?
    ): Response<Boolean> {
        return try{
            if(sid == null){
                return Response.Failure(Exception("sid is null"))
            }

            val cardCollectionRef = cardsRef.firestore
                .collection(USERS)
                .document(userId)
                .collection(CARDS)

            val querySnapshot = cardCollectionRef.whereEqualTo("sid", sid).get().await()

            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents.first()
                documentSnapshot.reference.delete().await()
                Response.Success(true)
            } else {
                Response.Failure(Exception("Card with sid $sid not found"))
            }
        }catch (e: Exception){
            Response.Failure(e)
        }
    }

    override suspend fun updateCardInFirestore(
        userId: String,
        sid: String?,
        words: Words
    ): Response<String> {
        return try {
            if(sid == null){
                return Response.Failure(Exception("sid is null"))
            }
            val cardCollectionRef = cardsRef.firestore
                .collection(USERS)
                .document(userId)
                .collection(CARDS)

            val cardDocRef = cardCollectionRef.document(sid)

            val updatedCardData = hashMapOf(
                "cardId" to words.wordsId,
                "origin" to words.origin,
                "translated" to words.translated,
                "sourceLangCode" to words.sourceLangCode,
                "targetLangCode" to words.targetLangCode,
                "sync" to words.sync,
                "sid" to sid
            )
            cardDocRef.set(updatedCardData).await()
            val newSid = cardDocRef.id
            Response.Success(newSid)
        }catch (e: Exception){
            Response.Failure(e)
        }
    }
}