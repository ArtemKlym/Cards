package com.artemklymenko.cards.view.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.artemklymenko.cards.databinding.FragmentHomeBinding
import com.artemklymenko.cards.view.activities.AddWordsActivity
import com.artemklymenko.cards.view.activities.CardsActivity
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
        val dataStorePreferenceManager = DataStorePreferenceManager.getInstance(container!!.context)
        binding.apply {
            btnAddWord.setOnClickListener {
                startActivity(Intent(context, AddWordsActivity::class.java))
            }
            btnStartLearning.setOnClickListener {
                startActivity(Intent(context, CardsActivity::class.java))
            }
            lifecycleScope.launch {
                dataStorePreferenceManager.consecutiveDaysFlow.collect { consecutiveDays ->
                    tvDays.text = consecutiveDays.toString()
                }
            }
        }
        return binding.root
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