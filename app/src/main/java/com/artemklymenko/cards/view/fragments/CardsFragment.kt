package com.artemklymenko.cards.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemklymenko.cards.R
import com.artemklymenko.cards.adapters.WordsAdapter
import com.artemklymenko.cards.databinding.FragmentCardsBinding
import com.artemklymenko.cards.db.Words
import com.artemklymenko.cards.vm.WordsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardsFragment : Fragment() {

    private var _binding: FragmentCardsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WordsViewModel by viewModels()
    private lateinit var listWords:List<Words>
    private lateinit var wordsAdapter: WordsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        checkItems()

        binding.searchViewWords.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(text: String?): Boolean {
                binding.searchViewWords.clearFocus()
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
                filterList(text)
                return true
            }
        })
    }

    private fun filterList(text: String?) {
        if (text.isNullOrEmpty()) {
            wordsAdapter.differ.submitList(listWords)
        } else {
            val filteredList = listWords.filter { word ->
                word.origin.contains(text, ignoreCase = true) || word.translated.contains(text, ignoreCase = true)
            }
            wordsAdapter.differ.submitList(filteredList)
        }
    }

    override fun onResume() {
        super.onResume()
        checkItems()
    }

    private fun checkItems() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = viewModel.getAllWords()
            CoroutineScope(Dispatchers.Main).launch {
                listWords = list
                wordsAdapter.differ.submitList(list)
            }
        }
    }

    private fun setupRecyclerView() {
        wordsAdapter = WordsAdapter { word ->
            val bundle = Bundle().apply {
                putInt("wordsId", word.wordsId)
                putString("sid", word.sid)
                putString("sourceCode", word.sourceLangCode)
                putString("targetCode", word.targetLangCode)
            }

            val updateCardFragment = UpdateCardFragment().apply {
                arguments = bundle
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, updateCardFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.recyclerViewWords.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = wordsAdapter
        }
    }

    companion object{
        @JvmStatic
        fun newInstance() = CardsFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}