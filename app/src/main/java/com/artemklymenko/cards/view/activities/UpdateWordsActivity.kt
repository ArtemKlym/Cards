package com.artemklymenko.cards.view.activities

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.artemklymenko.cards.R
import com.artemklymenko.cards.databinding.ActivityUpdateWordsBinding
import com.artemklymenko.cards.db.Words
import com.artemklymenko.cards.firestore.model.Response
import com.artemklymenko.cards.utils.Network
import com.artemklymenko.cards.vm.FirestoreViewModel
import com.artemklymenko.cards.vm.LoginViewModel
import com.artemklymenko.cards.vm.WordsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UpdateWordsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateWordsBinding

    private val wordsViewModel: WordsViewModel by viewModels()
    private val firestoreViewModel: FirestoreViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    private var wordsId: Int = 0
    private var sid: String? = null
    private var source: String? = null
    private var target: String? = null
    private var logIn = false

    override fun onStart() {
        super.onStart()
        logIn = loginViewModel.currentUser != null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateWordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.extras?.let {
            wordsId = it.getInt("wordsId")
            sid = it.getString("sid")
            source = it.getString("sourceCode")
            target = it.getString("targetCode")
        }

        binding.apply {
            showSelectedCard(wordsId)

            btnUpdate.setOnClickListener {
                val card = Words(
                    wordsId, etUpdateOrigin.text.toString(),
                    etUpdateTranslated.text.toString(),
                    sourceLangCode = source!!,
                    targetLangCode = target!!
                )
                if (logIn && Network.isConnected(this@UpdateWordsActivity)) {
                    firestoreViewModel.updateCardFromFirestore(
                        loginViewModel.currentUser!!.uid,
                        sid,
                        card
                    )
                    firestoreViewModel.updateResultCard.observe(this@UpdateWordsActivity) { response ->
                        if (response is Response.Success) {
                            val result = wordsViewModel.updateWords(card.copy(sid = response.data))
                            checkResult(
                                result,
                                this@UpdateWordsActivity.getString(R.string.updated),
                                this@UpdateWordsActivity
                            )
                        }
                    }
                } else {
                    val result = wordsViewModel.updateWords(card)
                    checkResult(
                        result,
                        this@UpdateWordsActivity.getString(R.string.updated),
                        this@UpdateWordsActivity
                    )
                }
            }

            btnDelete.setOnClickListener {
                val card = Words(
                    wordsId, etUpdateOrigin.text.toString(),
                    etUpdateTranslated.text.toString()
                )
                if (logIn && Network.isConnected(this@UpdateWordsActivity)) {
                    firestoreViewModel.deleteCardFromFirestore(
                        loginViewModel.currentUser!!.uid,
                        sid
                    )
                    firestoreViewModel.deleteCardResult.observe(this@UpdateWordsActivity) { response ->
                        if (response is Response.Success) {
                            val result = wordsViewModel.deleteWords(card)
                            checkResult(
                                result,
                                this@UpdateWordsActivity.getString(R.string.deleted),
                                this@UpdateWordsActivity
                            )
                        } else {
                            Toast.makeText(
                                this@UpdateWordsActivity,
                                response.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    val result = wordsViewModel.deleteWords(card)
                    checkResult(
                        result,
                        this@UpdateWordsActivity.getString(R.string.deleted),
                        this@UpdateWordsActivity
                    )
                }
            }
        }
    }

    private fun checkResult(result: Response<Boolean>, action: String, context: Context) {
        if (result is Response.Success) {
            val message = context.getString(R.string.card_has_been) + " $action"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            binding.apply {
                etUpdateOrigin.error = context.getString(R.string.incorrect_field)
                etUpdateTranslated.error = context.getString(R.string.incorrect_field)
            }
        }
    }

    private fun showSelectedCard(wordsId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val words = wordsViewModel.getWords(wordsId)
            runOnUiThread {
                binding.etUpdateOrigin.setText(words.origin)
                binding.etUpdateTranslated.setText(words.translated)
            }
        }
    }
}