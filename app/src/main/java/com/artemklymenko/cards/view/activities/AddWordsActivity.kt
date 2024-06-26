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
import com.artemklymenko.cards.db.Words
import com.artemklymenko.cards.di.ModelLanguage
import com.artemklymenko.cards.firestore.model.Response
import com.artemklymenko.cards.utils.Network
import com.artemklymenko.cards.utils.SpinnerUtils
import com.artemklymenko.cards.utils.TextWatcherUtils
import com.artemklymenko.cards.vm.FirestoreViewModel
import com.artemklymenko.cards.vm.LoginViewModel
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
    private lateinit var sourceLangCode: String
    private lateinit var targetLangCode: String
    private var logIn = false

    private var languageArrayList: List<ModelLanguage> = emptyList()
    private val translationViewModel: TranslateViewModel by viewModels()
    private val wordsViewModel: WordsViewModel by viewModels()
    private val firestoreViewModel: FirestoreViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    companion object {
        private const val TAG = "AddWordsTag"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logIn = loginViewModel.currentUser != null
        Log.d(TAG, "current user = $logIn")

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
            sourceLangCode = selectedLanguage.languageCode
        }

        SpinnerUtils.setOnItemSelectedListener(spinnerTargetLang) { position ->
            val selectedLanguage = languageArrayList[position]
            translationViewModel.setTargetLanguage(selectedLanguage.languageCode)
            targetLangCode = selectedLanguage.languageCode
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
                val words = Words(
                    0, sourceLang.text!!.toString(),
                    targetLang.text!!.toString(), sourceLangCode, targetLangCode
                )
                if(logIn && Network.isConnected(this)){
                    firestoreViewModel.addCardToFirestore(loginViewModel.currentUser!!.uid, words)
                    firestoreViewModel.addCardResult.observe(this@AddWordsActivity) { response ->
                        if (response is Response.Success) {
                            val result = wordsViewModel.insertWords(words.copy(sid = response.data))
                            checkResult(result)
                        }
                    }
                } else {
                    val result = wordsViewModel.insertWords(words)
                    checkResult(result)
                }
            } else {
                binding.tilOrigin.error = getString(R.string.enter_text_to_translate)
                binding.tilTranslated.error = getString(R.string.enter_text_to_translate)
            }
        }
    }

    private fun checkResult(result: Response<Boolean>) {
        if (result is Response.Success) {
            binding.apply {
                sourceLang.text!!.clear()
                targetLang.text!!.clear()
                translateBtn.isEnabled = false
                tilOrigin.error = null
            }
            Toast.makeText(
                this,
                "${getString(R.string.card_has_been)} ${getString(R.string.added)}",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(this, getString(R.string.failed_to_add_a_card), Toast.LENGTH_SHORT)
                .show()
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
            binding.tvModel.text = null
            Log.e(TAG, "startTranslation: ${e.printStackTrace()}")
            Toast.makeText(this, getString(R.string.failed_to_perform_automatic_translation), Toast.LENGTH_SHORT).show()
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
