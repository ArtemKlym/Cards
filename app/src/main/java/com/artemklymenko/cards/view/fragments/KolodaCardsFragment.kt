package com.artemklymenko.cards.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.artemklymenko.cards.adapters.SwipeAdapter
import com.artemklymenko.cards.databinding.FragmentKolodaCardsBinding
import com.artemklymenko.cards.db.Words
import com.artemklymenko.cards.vm.WordsViewModel
import com.yalantis.library.Koloda
import com.yalantis.library.KolodaListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class KolodaCardsFragment : Fragment() {
    private var _binding: FragmentKolodaCardsBinding? = null
    private val binding get() = _binding!!

    private lateinit var swipeAdapter: SwipeAdapter

    private lateinit var arrayList: ArrayList<Words>

    private val viewModel: WordsViewModel by viewModels()

    private lateinit var koloda: Koloda

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKolodaCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    companion object {
        @JvmStatic
        fun newInstance() {}
    }

    private fun initializeProgressBar(listSize: Int) {
        binding.progressBar.max = listSize
    }

    private fun getList() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = viewModel.getAllWords()
            requireActivity().runOnUiThread {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}