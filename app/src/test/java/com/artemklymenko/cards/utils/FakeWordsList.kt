package com.artemklymenko.cards.utils

import com.artemklymenko.cards.db.Words

fun getUsersWordsList(): Map<String, List<Words>> {
    return mapOf(
        "1" to listOf(
            Words(
                wordsId = 0,
                origin = "conducive",
                translated = "що сприяють",
                exampleOfUse = "making a certain situation or outcome",
                priority = 16,
                lastSeen = 5L,
                sourceLangCode = "en",
                targetLangCode = "uk",
                sid = "kmfkdmb",
                sync = 9L,
            ),
            Words(
                wordsId = 1,
                origin = "apart from",
                translated = "окремо від",
                exampleOfUse = "besides",
                priority = 6,
                lastSeen = 5L,
                sourceLangCode = "en",
                targetLangCode = "uk",
                sid = "ewqrtb",
                sync = 9L,
            )
        ),
        "2" to listOf(
            Words(
                wordsId = 0,
                origin = "hello",
                translated = "привіт",
                exampleOfUse = "",
                priority = 16,
                lastSeen = 5L,
                sourceLangCode = "en",
                targetLangCode = "uk",
                sid = "pninknm",
                sync = 9L,
            ),
            Words(
                wordsId = 1,
                origin = "run down",
                translated = "виснажуватися",
                exampleOfUse = "I feel run down after working all week",
                priority = 6,
                lastSeen = 5L,
                sourceLangCode = "en",
                targetLangCode = "uk",
                sid = "mbvcnmgkj",
                sync = 9L,
            )
        )
    )
}

fun getFakeWord(): Words {
    return Words(
        wordsId = 3,
        origin = "run down",
        translated = "виснажуватися",
        exampleOfUse = "I feel run down after working all week",
        priority = 6,
        lastSeen = 5L,
        sourceLangCode = "en",
        targetLangCode = "uk",
        sid = "kmfkdmb",
        sync = 9L,
    )
}