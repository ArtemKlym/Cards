package com.artemklymenko.cards.view.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.artemklymenko.cards.adaptar.SwipeAdapter
import com.artemklymenko.cards.databinding.ActivityCardsBinding
import com.artemklymenko.cards.db.Words
import com.artemklymenko.cards.vm.WordsViewModel
import com.yalantis.library.Koloda
import com.yalantis.library.KolodaListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardsBinding

    private lateinit var wordsList: List<Words>

    private lateinit var swipeAdapter: SwipeAdapter

    private lateinit var arrayList: ArrayList<Words>

    private val viewModel: WordsViewModel by viewModels()

    private lateinit var koloda: Koloda

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        koloda = binding.koloda

        getList()

        koloda.kolodaListener = object : KolodaListener {

            override fun onCardSwipedLeft(position: Int) {
                arrayList = wordsList as ArrayList<Words>
                swipeAdapter.addSwiped(arrayList[position + 1])
                arrayList.add(arrayList[position + 1])
                updateProgressBar(arrayList.size, -1)
            }

            override fun onCardSwipedRight(position: Int) {
                updateProgressBar(arrayList.size - 1, 1)
            }
        }
    }

    private fun getList() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = viewModel.getAllWords()
            runOnUiThread {
                wordsList = list
                setupAdapter(wordsList)
            }
        }
    }

    private fun setupAdapter(list: List<Words>) {
        swipeAdapter = SwipeAdapter(list)
        koloda.adapter = swipeAdapter
    }

    private fun updateProgressBar(listSize: Int, currentProgress: Int) {
        binding.progressBar.max = listSize
        binding.progressBar.progress += currentProgress
    }
}
