package com.artemklymenko.cards.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.artemklymenko.cards.utils.Constants.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class Words(
    @PrimaryKey(autoGenerate = true)
    val wordsId: Int = 0,
    val origin: String = "",
    val translated: String = "",
    val exampleOfUse: String = "",
    var priority: Int = 10,
    var lastSeen: Long = 0L,
    val sourceLangCode: String ="",
    val targetLangCode: String = "",
    var sid: String? = null,
    val sync: Long = System.currentTimeMillis()
)
