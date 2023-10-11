package com.artemklymenko.cards.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.artemklymenko.cards.utils.Constants.TABLE_NAME

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: Words)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateWords(words: Words)

    @Delete
    suspend fun deleteWords(words: Words)

    @Query("SELECT * FROM $TABLE_NAME ORDER BY wordsId ASC")
    suspend fun getAllWords(): List<Words>

    @Query("SELECT * FROM $TABLE_NAME WHERE wordsId like :id")
    suspend fun getWords(id: Int): Words
}