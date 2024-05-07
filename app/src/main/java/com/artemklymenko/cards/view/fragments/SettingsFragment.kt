package com.artemklymenko.cards.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.artemklymenko.cards.R
import com.artemklymenko.cards.databinding.FragmentSettingsBinding
import com.artemklymenko.cards.notification.utils.setupNotificationsSwitch
import com.artemklymenko.cards.vm.DataStorePreferenceManager
import com.artemklymenko.cards.vm.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val dataStorePreferenceManager = DataStorePreferenceManager.getInstance(container!!.context)
        binding.switchNotification.isChecked = dataStorePreferenceManager.notice

        if (viewModel.currentUser?.email != null) {
            binding.tvUserEmail.text = viewModel.currentUser!!.email
        } else {
            binding.tvUserEmail.text = getString(R.string.user)
            }

        binding.ivLogOut.setOnClickListener {
            viewModel.logout()
            switchToSignInFragment()
        }

        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            setupNotificationsSwitch(
                dataStorePreferenceManager,
                isChecked,
                container.context
            )
        }

        return binding.root
    }

    private fun switchToSignInFragment() {
        val signInFragment = SignInFragment.newInstance()
        parentFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, signInFragment)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}