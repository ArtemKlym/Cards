package com.artemklymenko.cards.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemklymenko.cards.adaptar.WordsAdapter
import com.artemklymenko.cards.databinding.FragmentCardsBinding
import com.artemklymenko.cards.vm.WordsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CardsFragment : Fragment() {

    private lateinit var binding: FragmentCardsBinding
    private val viewModel: WordsViewModel by viewModels()

    @Inject
    lateinit var wordsAdapter: WordsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardsBinding.inflate(inflater, container, false)

        checkItems()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        checkItems()
    }

    private fun checkItems() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = viewModel.getAllWords()
            CoroutineScope(Dispatchers.Main).launch {
                wordsAdapter.differ.submitList(list)
                setupRecyclerView()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewWords.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = wordsAdapter
        }
    }

    companion object{

        @JvmStatic
        fun newInstance() = CardsFragment()
    }
}