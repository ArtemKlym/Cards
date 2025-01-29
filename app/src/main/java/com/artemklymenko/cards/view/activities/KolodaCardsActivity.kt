package com.artemklymenko.cards.view.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.artemklymenko.cards.adapters.SwipeAdapter
import com.artemklymenko.cards.databinding.ActivityKolodaCardsBinding
import com.artemklymenko.cards.db.Words
import com.artemklymenko.cards.vm.WordsViewModel
import com.yalantis.library.Koloda
import com.yalantis.library.KolodaListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class KolodaCardsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKolodaCardsBinding

    private lateinit var swipeAdapter: SwipeAdapter

    private lateinit var arrayList: ArrayList<Words>

    private val viewModel: WordsViewModel by viewModels()

    private lateinit var koloda: Koloda

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityKolodaCardsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        koloda = binding.koloda

        getList()

        koloda.kolodaListener = object : KolodaListener {

            override fun onCardSwipedLeft(position: Int) {
                val repeatWord = arrayList[position + 1]
                swipeAdapter.addSwiped(repeatWord)
                arrayList.add(repeatWord)
                binding.groupRepeatKnow.visibility = View.INVISIBLE
            }

            override fun onCardSwipedRight(position: Int) {
                updateProgressBar()
                binding.groupRepeatKnow.visibility = View.INVISIBLE
            }

            override fun onCardDrag(position: Int, cardView: View, progress: Float) {
                binding.groupRepeatKnow.visibility = View.VISIBLE
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
}