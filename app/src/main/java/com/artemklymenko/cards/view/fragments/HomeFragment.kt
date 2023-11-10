package com.artemklymenko.cards.view.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.artemklymenko.cards.databinding.FragmentHomeBinding
import com.artemklymenko.cards.view.activities.AddWordsActivity
import com.artemklymenko.cards.view.activities.CardsActivity

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.apply {
            btnAddWord.setOnClickListener {
                startActivity(Intent(context, AddWordsActivity::class.java))
            }
            btnStartLearning.setOnClickListener {
                startActivity(Intent(context, CardsActivity::class.java))
            }
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}