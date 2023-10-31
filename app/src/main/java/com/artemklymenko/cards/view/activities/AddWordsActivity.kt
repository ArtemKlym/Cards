package com.artemklymenko.cards.view.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.artemklymenko.cards.databinding.ActivityAddWordsBinding
import com.artemklymenko.cards.di.ModelLanguage
import com.artemklymenko.cards.vm.TranslateViewModel
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

        spinnerSourceLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                val selectedLanguage = languageArrayList[i]
                translationViewModel.setSourceLanguage(selectedLanguage.languageCode)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        spinnerTargetLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                val selectedLanguage = languageArrayList[i]
                translationViewModel.setTargetLanguage(selectedLanguage.languageCode)
                if(sourceLang.text!!.isNotEmpty()){
                    validateData()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        sourceLang.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                validateData()
            }
        })

        translateBtn.setOnClickListener {

        }
    }

    private fun setupViews() {
        sourceLang = binding.etOrigin
        targetLang = binding.etTranslated
        spinnerSourceLang = binding.spOrigin
        spinnerTargetLang = binding.spTranslated
        translateBtn = binding.btnAdd
    }

    private fun setupSpinners() {
        val languageAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageArrayList.map { it.languageTitle })
        spinnerSourceLang.adapter = languageAdapter
        spinnerTargetLang.adapter = languageAdapter
    }

    private fun validateData() {
        val sourceLangText = sourceLang.text.toString().trim()
        if (sourceLangText.isEmpty()) {
            binding.tilOrigin.error = "Enter text to translate"
        } else {
            startTranslation()
            binding.tilOrigin.error = null
        }
    }

    private fun startTranslation() {
        val sourceText = sourceLang.text.toString()
        translationViewModel.translateText(sourceText, { translatedText ->
            binding.tvModel.text = "Translating..."
            targetLang.setText(translatedText)
            translateBtn.isEnabled = sourceLang.text!!.isNotEmpty() && targetLang.text!!.isNotEmpty()
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
