package com.artemklymenko.cards.di

import com.artemklymenko.cards.firestore.repository.AuthRepository
import com.artemklymenko.cards.firestore.repository.impl.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent:: class)
@Module
class AuthModule {

    @Provides
    fun providesFireBaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun providesAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl
}