package com.artemklymenko.cards.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.artemklymenko.cards.databinding.FragmentDonationBinding
import com.artemklymenko.cards.utils.TextWatcherUtils
import com.artemklymenko.cards.view.activities.CheckoutActivity


class DonationFragment : Fragment() {

    private var _binding: FragmentDonationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDonationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            customAmount.addTextChangedListener(TextWatcherUtils.afterTextChanged { text ->
                if (!text.isNullOrEmpty()) {
                    radioGroupDonation.clearCheck()
                }
            })

            radioGroupDonation.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId != -1) {
                    customAmount.clearFocus()
                    customAmount.text = null
                }
            }

            btnContinue.setOnClickListener {
                val selectedRadioButton = radioGroupDonation.checkedRadioButtonId
                val amount = if (selectedRadioButton != -1) {
                    val selectedButton = root.findViewById<RadioButton>(selectedRadioButton)
                    selectedButton.text.toString().replace("$", "")
                } else if (!customAmount.text.isNullOrEmpty()) {
                    customAmount.text.toString()
                } else {
                    Toast.makeText(requireContext(), "Please select or enter an amount", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val intent = Intent(requireContext(), CheckoutActivity::class.java)
                intent.putExtra("amount", amount)
                startActivity(intent)
                // Use the selected amount (debugging log)
                Log.d("Donation", "Selected Amount: $amount")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = DonationFragment()
    }
}