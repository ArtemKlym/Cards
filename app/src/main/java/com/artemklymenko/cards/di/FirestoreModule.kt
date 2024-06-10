package com.artemklymenko.cards.di

import com.artemklymenko.cards.firestore.repository.impl.CardsRepositoryImpl
import com.artemklymenko.cards.firestore.repository.CardsRepository
import com.artemklymenko.cards.utils.Constants.CARDS
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FirestoreModule {

    @Provides
    fun providesCardsRef() = Firebase.firestore.collection(CARDS)

    @Provides
    fun providesCardsRepository(
        cardsRef: CollectionReference
    ): CardsRepository = CardsRepositoryImpl(cardsRef)
}