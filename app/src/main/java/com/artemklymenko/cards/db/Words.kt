package com.artemklymenko.cards.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.artemklymenko.cards.utils.Constants.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class Words(
    @PrimaryKey(autoGenerate = true)
    val wordsId: Int = 0,
    val origin: String = "",
    val translated: String = ""
)
