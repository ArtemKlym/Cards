package com.artemklymenko.cards.utils

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

class SpinnerUtils {
    companion object {
        fun setOnItemSelectedListener(spinner: Spinner, onItemSelected: (Int) -> Unit) {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    onItemSelected(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }
}