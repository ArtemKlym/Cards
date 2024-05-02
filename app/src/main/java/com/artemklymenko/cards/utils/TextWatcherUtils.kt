package com.artemklymenko.cards.utils

import android.text.Editable
import android.text.TextWatcher

class TextWatcherUtils {
    companion object {
        fun afterTextChanged(afterTextChanged: (Editable?) -> Unit): TextWatcher {
            return object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    afterTextChanged(s)
                }
            }
        }
    }
}