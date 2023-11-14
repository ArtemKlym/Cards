package com.artemklymenko.cards.view.activities

import android.os.Bundle
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
                val repeatWord = arrayList[position + 1]
                swipeAdapter.addSwiped(repeatWord)
                arrayList.add(repeatWord)
                changeHintVisibilityInvisible()
            }

            override fun onCardSwipedRight(position: Int) {
                updateProgressBar()
                changeHintVisibilityInvisible()
            }

            override fun onCardDrag(position: Int, cardView: View, progress: Float) {
                changeHintVisibilityVisible()
            }
        }
    }

    private fun initializeProgressBar(listSize: Int) {
        binding.progressBar.max = listSize
    }

    private fun getList() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = viewModel.getAllWords()
            runOnUiThread {
                initializeProgressBar(list.size)
                setupAdapter(list)
                arrayList = list as ArrayList<Words>
            }
        }
    }

    private fun setupAdapter(list: List<Words>) {
        swipeAdapter = SwipeAdapter(list)
        koloda.adapter = swipeAdapter
    }

    private fun updateProgressBar() {
        binding.progressBar.progress += 1
    }

    private fun changeHintVisibilityVisible() {
        binding.apply {
                tvRepeat.visibility = View.VISIBLE
                tvKnow.visibility = View.VISIBLE
                ivHintLeft.visibility = View.VISIBLE
                ivHintRight.visibility = View.VISIBLE
        }
    }

    private fun changeHintVisibilityInvisible(){
        binding.apply {
            tvRepeat.visibility = View.INVISIBLE
            tvKnow.visibility = View.INVISIBLE
            ivHintLeft.visibility = View.INVISIBLE
            ivHintRight.visibility = View.INVISIBLE
        }
    }
}
