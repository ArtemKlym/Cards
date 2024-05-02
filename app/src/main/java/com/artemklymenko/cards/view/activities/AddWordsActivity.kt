package com.artemklymenko.cards.view.activities

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.artemklymenko.cards.R
import com.artemklymenko.cards.databinding.ActivityAddWordsBinding
import com.artemklymenko.cards.di.ModelLanguage
import com.artemklymenko.cards.utils.SpinnerUtils
import com.artemklymenko.cards.utils.TextWatcherUtils
import com.artemklymenko.cards.vm.TranslateViewModel
import com.artemklymenko.cards.vm.WordsViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.mlkit.nl.translate.TranslateLanguage
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

    private var languageArrayList: List<ModelLanguage> = emptyList()
    private val translationViewModel: TranslateViewModel by viewModels()
    private val wordsViewModel: WordsViewModel by viewModels()

    companion object {
        private const val TAG = "MAIN_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadAvailableLanguages()

        setupViews()
        setupSpinners()
        setListeners()
    }

    private fun setupViews() {
        sourceLang = binding.etOrigin
        targetLang = binding.etTranslated
        spinnerSourceLang = binding.spOrigin
        spinnerTargetLang = binding.spTranslated
        translateBtn = binding.btnAdd
    }

    private fun setupSpinners() {
        val languageAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            languageArrayList.map { it.languageTitle })
        spinnerSourceLang.adapter = languageAdapter
        spinnerTargetLang.adapter = languageAdapter
    }

    private fun setListeners() {
        SpinnerUtils.setOnItemSelectedListener(spinnerSourceLang) { position ->
            val selectedLanguage = languageArrayList[position]
            translationViewModel.setSourceLanguage(selectedLanguage.languageCode)
        }

        SpinnerUtils.setOnItemSelectedListener(spinnerTargetLang) { position ->
            val selectedLanguage = languageArrayList[position]
            translationViewModel.setTargetLanguage(selectedLanguage.languageCode)
            if (sourceLang.text!!.isNotEmpty()) {
                validateData()
            }
        }

        sourceLang.addTextChangedListener(TextWatcherUtils.afterTextChanged {
            binding.tilOrigin.error = null
            binding.tilTranslated.error = null
            validateData()
        })

        translateBtn.setOnClickListener {
            if (checkFields()) {
                val result = wordsViewModel.insertWords(
                    sourceLang.text!!.toString(),
                    targetLang.text!!.toString()
                )
                checkResult(result)
            } else {
                binding.tilOrigin.error = getString(R.string.enter_text_to_translate)
                binding.tilTranslated.error = getString(R.string.enter_text_to_translate)
            }
        }
    }

    private fun checkResult(result: Boolean) {
        if (result) {
            binding.apply {
                sourceLang.text!!.clear()
                targetLang.text!!.clear()
                translateBtn.isEnabled = false
                tilOrigin.error = null
            }
        } else {
            Toast.makeText(this, "Failed to add a card", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkFields(): Boolean {
        return sourceLang.text!!.isNotEmpty() && targetLang.text!!.isNotEmpty()
    }

    private fun validateData() {
        val sourceLangText = sourceLang.text.toString().trim()
        if (sourceLangText.isEmpty()) {
            binding.tilOrigin.error = getString(R.string.enter_text_to_translate)
        } else {
            startTranslation()
            binding.tilOrigin.error = null
        }
    }

    private fun startTranslation() {
        val sourceText = sourceLang.text.toString()
        binding.tvModel.text = getString(R.string.translating)
        translationViewModel.translateText(sourceText, { translatedText ->
            binding.tvModel.text = null
            targetLang.setText(translatedText)
            translateBtn.isEnabled =
                sourceLang.text!!.isNotEmpty() && targetLang.text!!.isNotEmpty()
        }, { e ->
            Log.d(TAG, "startTranslation: $e")
            Toast.makeText(this, "Failed to translate due to: $e", Toast.LENGTH_SHORT).show()
        })
    }

    private fun loadAvailableLanguages() {
        val languageCodeList = TranslateLanguage.getAllLanguages()
        languageArrayList = languageCodeList.map { languageCode ->
            val languageTitle = Locale(languageCode).displayLanguage
            ModelLanguage(languageCode, languageTitle)
        }
    }
}
