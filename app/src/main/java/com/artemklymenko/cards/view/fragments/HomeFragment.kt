package com.artemklymenko.cards.view.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.artemklymenko.cards.R
import com.artemklymenko.cards.databinding.FragmentHomeBinding
import com.artemklymenko.cards.view.activities.AddWordsActivity
import com.artemklymenko.cards.vm.DataStorePreferenceManager
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            val dataStorePreferenceManager = DataStorePreferenceManager.getInstance(requireContext())
            binding.apply {
                btnAddWord.setOnClickListener {
                    startActivity(Intent(context, AddWordsActivity::class.java))
                }
                btnStartLearning.setOnClickListener {
                    val kolodaCardsFragment = KolodaCardsFragment()
                    parentFragmentManager
                        .beginTransaction()
                        .replace(R.id.frameLayout, kolodaCardsFragment)
                        .commit()
                }
                lifecycleScope.launch {
                    dataStorePreferenceManager.consecutiveDaysFlow.collect { consecutiveDays ->
                        tvDays.text = consecutiveDays.toString()
                    }
                }
            }
    }
    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}