package com.artemklymenko.cards.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Words::class],
    version = 1
)
abstract class WordDB: RoomDatabase() {
    abstract fun wordsDao(): WordDao
}