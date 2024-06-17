package com.artemklymenko.cards.vm

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class TranslateViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : AndroidViewModel(context as Application){

    private var sourceLangCode = "en"
    private var targetLangCode = "uk"

    fun setSourceLanguage(languageCode: String) {
        sourceLangCode = languageCode
    }

    fun setTargetLanguage(languageCode: String) {
        targetLangCode = languageCode
    }
    
   fun translateText(
        sourceText: String,
        onTranslationResult: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val translatorOptions = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLangCode)
            .setTargetLanguage(targetLangCode)
            .build()

        val translator = Translation.getClient(translatorOptions)

        val downloadConditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translator.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener {
                translator.translate(sourceText)
                    .addOnSuccessListener {
                        onTranslationResult(it)
                    }
                    .addOnFailureListener { e ->
                        Log.e("TranslationError", "Translation failed", e)
                        onError(e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("TranslationError", "Model download failed with Exception: ${e.localizedMessage}", e)
                onError(e)
            }
    }
}