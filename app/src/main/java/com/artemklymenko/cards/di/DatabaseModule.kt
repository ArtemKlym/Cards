package com.artemklymenko.cards.di

import android.content.Context
import androidx.room.Room
import com.artemklymenko.cards.db.WordDB
import com.artemklymenko.cards.db.Words
import com.artemklymenko.cards.utils.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWordsDB(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        WordDB::class.java,
        DATABASE_NAME
    ).build()

    @Provides
    fun provideWordsDao(db: WordDB) = db.wordsDao()

    @Provides
    fun provideWords() = Words()
}