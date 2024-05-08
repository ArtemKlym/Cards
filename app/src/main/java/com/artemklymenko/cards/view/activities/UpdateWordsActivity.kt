package com.artemklymenko.cards.view.activities

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.artemklymenko.cards.R
import com.artemklymenko.cards.databinding.ActivityUpdateWordsBinding
import com.artemklymenko.cards.vm.WordsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UpdateWordsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateWordsBinding

    private val viewModel: WordsViewModel by viewModels()

    private var wordsId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateWordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.extras?.let {
            wordsId = it.getInt("wordsId")
        }

        binding.apply {
            showSelectedCard(wordsId)

            btnUpdate.setOnClickListener {
                val result = viewModel.updateWords(
                    wordsId,
                    etUpdateOrigin.text.toString(),
                    etUpdateTranslated.text.toString()
                )
                checkResult(
                    result,
                    this@UpdateWordsActivity.getString(R.string.updated),
                    this@UpdateWordsActivity
                )
            }

            btnDelete.setOnClickListener {
                val result = viewModel.deleteWords(
                    wordsId,
                    etUpdateOrigin.text.toString(),
                    etUpdateTranslated.text.toString()
                )
                checkResult(
                    result,
                    this@UpdateWordsActivity.getString(R.string.deleted),
                    this@UpdateWordsActivity
                )
            }
        }
    }

    private fun checkResult(result: Boolean, action: String, context: Context) {
        if (result) {
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
            val words = viewModel.getWords(wordsId)
            runOnUiThread {
                binding.etUpdateOrigin.setText(words.origin)
                binding.etUpdateTranslated.setText(words.translated)
            }
        }
    }
}