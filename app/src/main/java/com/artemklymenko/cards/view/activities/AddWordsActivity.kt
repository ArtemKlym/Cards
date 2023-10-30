package com.artemklymenko.cards.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.artemklymenko.cards.R
import com.artemklymenko.cards.databinding.ActivityAddWordsBinding
import com.artemklymenko.cards.di.ModelLanguage
import com.artemklymenko.cards.vm.TranslateViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class AddWordsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddWordsBinding
    private lateinit var sourceLang: TextInputEditText
    private lateinit var targetLang: TextInputEditText
    private lateinit var spinnerSourceLang: Spinner
    private lateinit var spinnerTargetLang: Spinner
    private lateinit var translateBtn: Button

    private var languageArrayList: ArrayList<ModelLanguage>? = null

    private lateinit var translatorOptions: TranslatorOptions
    private lateinit var translator: Translator

    private var sourceLangCode = "en"
    private var targetLangCode = "uk"
    private var sourceLangTitle = "English"
    private var targetLangTitle = "Ukrainian"

    companion object {
        private const val TAG = "MAIN_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadAvailableLanguages()

        sourceLang = binding.etOrigin
        targetLang = binding.etTranslated

        spinnerSourceLang = binding.spOrigin
        spinnerTargetLang = binding.spTranslated

        translateBtn = binding.btnAdd

        val languageAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languageArrayList!!.map { it.languageTitle })
        spinnerTargetLang.adapter = languageAdapter
        spinnerSourceLang.adapter = languageAdapter

        spinnerSourceLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                sourceLanguageChoose(i)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Handle nothing selected if needed
            }
        }

        spinnerTargetLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                targetLanguageChoose(i)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Handle nothing selected if needed
            }
        }

        sourceLang.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Not needed
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Not needed
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d(TAG, "afterTextChanged called")
                validateData()
            }
        })
    }

    private fun validateData() {
        val sourceLangText = sourceLang.text.toString().trim()
        Log.d(TAG, "validateData: sourceLangText: $sourceLangText")

        if (sourceLangText.isEmpty()) {
            binding.tilOrigin.error = "Enter text to translate"
        } else {
            binding.tilOrigin.error = null
            startTranslation()
        }
    }

    private fun startTranslation() {
        translatorOptions = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLangCode)
            .setTargetLanguage(targetLangCode)
            .build()
        translator = Translation.getClient(translatorOptions)

        val downloadConditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translator.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener {
                Log.d(TAG, "startTranslation: translation model ready, start translation...")
                binding.tvModel.text = "Downloading model..."
                translator.translate(sourceLang.text.toString())
                    .addOnSuccessListener {
                        binding.tvModel.text = "Translating..."
                        targetLang.setText(it)
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG, "startTranslation: $e")
                        Toast.makeText(this, "Failed to translate due to: $e", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                // Handle model download failure if needed
            }
    }

    private fun loadAvailableLanguages() {
        languageArrayList = ArrayList()

        val languageCodeList = TranslateLanguage.getAllLanguages()

        for (languageCode in languageCodeList) {
            val languageTitle = Locale(languageCode).displayLanguage

            Log.d(TAG, "loadAvailableLanguages: languageCode: $languageCode")
            Log.d(TAG, "loadAvailableLanguages: languageTitle: $languageTitle")

            val modelLanguage = ModelLanguage(languageCode, languageTitle)
            languageArrayList!!.add(modelLanguage)
        }
    }

    private fun sourceLanguageChoose(position: Int) {
        val selectedLanguage = languageArrayList!![position]
        sourceLangCode = selectedLanguage.languageCode
        sourceLangTitle = selectedLanguage.languageTitle
        Log.d(TAG, "sourceLanguageChoose: sourceLanguageCode: $sourceLangCode")
        Log.d(TAG, "sourceLanguageChoose: sourceLanguageTitle: $sourceLangTitle")
    }

    private fun targetLanguageChoose(position: Int) {
        val selectedLanguage = languageArrayList!![position]
        targetLangCode = selectedLanguage.languageCode
        targetLangTitle = selectedLanguage.languageTitle
        Log.d(TAG, "targetLanguageChoose: targetLangCode: $targetLangCode")
        Log.d(TAG, "targetLanguageChoose: targetLangTitle: $targetLangTitle")
    }
}