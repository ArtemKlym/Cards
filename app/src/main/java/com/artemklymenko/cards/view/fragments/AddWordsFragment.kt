package com.artemklymenko.cards.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.artemklymenko.cards.R
import com.artemklymenko.cards.databinding.FragmentAddWordsBinding
import com.artemklymenko.cards.db.Words
import com.artemklymenko.cards.di.ModelLanguage
import com.artemklymenko.cards.firestore.model.Response
import com.artemklymenko.cards.utils.Network
import com.artemklymenko.cards.utils.SpinnerUtils
import com.artemklymenko.cards.utils.TextWatcherUtils
import com.artemklymenko.cards.vm.DataStorePreferenceManager
import com.artemklymenko.cards.vm.FirestoreViewModel
import com.artemklymenko.cards.vm.LoginViewModel
import com.artemklymenko.cards.vm.TranslateViewModel
import com.artemklymenko.cards.vm.WordsViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.mlkit.nl.translate.TranslateLanguage
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class AddWordsFragment : Fragment() {

    companion object {
        const val TAG = "AddWordsTag"
        @JvmStatic
        fun newInstance() {}
    }

    private var _binding: FragmentAddWordsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sourceLang: TextInputEditText
    private lateinit var targetLang: TextInputEditText
    private lateinit var etExampleOfUse: TextInputEditText
    private lateinit var spinnerSourceLang: Spinner
    private lateinit var spinnerTargetLang: Spinner
    private lateinit var translateBtn: Button
    private lateinit var sourceLangCode: String
    private lateinit var targetLangCode: String
    private lateinit var selectedSourceLanguage: ModelLanguage
    private lateinit var selectedTargetLanguage: ModelLanguage
    private var logIn = false

    private var languageArrayList: List<ModelLanguage> = emptyList()
    private val translationViewModel: TranslateViewModel by viewModels()
    private val wordsViewModel: WordsViewModel by viewModels()
    private val firestoreViewModel: FirestoreViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var dataStorePreferenceManager: DataStorePreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddWordsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataStorePreferenceManager = DataStorePreferenceManager.getInstance(requireContext())

        logIn = loginViewModel.currentUser != null

        loadAvailableLanguages()
        setupViews()
        setupSpinners()
        setListeners()
    }

    override fun onPause() {
        super.onPause()
        with(dataStorePreferenceManager) {
            if(sourceLang != sourceLangCode) {
                sourceLang = sourceLangCode
            }
            if(targetLang != targetLangCode) {
                targetLang = targetLangCode
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupViews() {
        binding.apply {
            sourceLang = etOrigin
            targetLang = etTranslated
            spinnerSourceLang = spOrigin
            spinnerTargetLang = spTranslated
            translateBtn = btnAdd
            etExampleOfUse = etExample
        }
    }

    private fun setupSpinners() {
        val languageAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            languageArrayList.map { it.languageTitle })
        spinnerSourceLang.adapter = languageAdapter
        spinnerTargetLang.adapter = languageAdapter
    }

    private fun setListeners() {
        val initialSourceCode = dataStorePreferenceManager.sourceLang
        val initialTargetCode = dataStorePreferenceManager.targetLang

        val initialSourcePosition =
            languageArrayList.indexOfFirst { it.languageCode == initialSourceCode }
        val initialTargetPosition =
            languageArrayList.indexOfFirst { it.languageCode == initialTargetCode }

        if (initialSourcePosition != -1) {
            spinnerSourceLang.setSelection(initialSourcePosition)
        }
        if (initialTargetPosition != -1) {
            spinnerTargetLang.setSelection(initialTargetPosition)
        }
        SpinnerUtils.setOnItemSelectedListener(spinnerSourceLang) { position ->
            selectedSourceLanguage = languageArrayList[position]
            translationViewModel.setSourceLanguage(selectedSourceLanguage.languageCode)
            sourceLangCode = selectedSourceLanguage.languageCode
        }

        SpinnerUtils.setOnItemSelectedListener(spinnerTargetLang) { position ->
            selectedTargetLanguage = languageArrayList[position]
            translationViewModel.setTargetLanguage(selectedTargetLanguage.languageCode)
            targetLangCode = selectedTargetLanguage.languageCode
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
                    wordsId = 0,
                    origin = sourceLang.text.toString(),
                    translated = targetLang.text.toString(),
                    exampleOfUse = etExampleOfUse.text.toString(),
                    sourceLangCode =  sourceLangCode,
                    targetLangCode =  targetLangCode
                )
                if (logIn && Network.isConnected(requireContext())) {
                    firestoreViewModel.addCardToFirestore(loginViewModel.currentUser!!.uid, words)
                    firestoreViewModel.addCardResult.observe(viewLifecycleOwner) { response ->
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
                requireContext(),
                "${getString(R.string.card_has_been)} ${getString(R.string.added)}",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(requireContext(), getString(R.string.failed_to_add_a_card), Toast.LENGTH_SHORT)
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
            Toast.makeText(
                requireContext(),
                getString(R.string.failed_to_perform_automatic_translation),
                Toast.LENGTH_SHORT
            ).show()
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