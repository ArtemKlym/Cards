package com.artemklymenko.cards.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.artemklymenko.cards.R
import com.artemklymenko.cards.firestore.model.Resource
import com.artemklymenko.cards.databinding.FragmentSignInBinding
import com.artemklymenko.cards.domain.validation.handleLoginValidation
import com.artemklymenko.cards.notification.utils.setupNotificationsSwitch
import com.artemklymenko.cards.vm.DataStorePreferenceManager
import com.artemklymenko.cards.vm.LoginViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LoginViewModel
    private var loginFlow: StateFlow<Resource<FirebaseUser>?>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]
        loginFlow = viewModel.loginFlow
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        val dataStorePreferenceManager = DataStorePreferenceManager.getInstance(container!!.context)
        binding.switchNotification.isChecked = dataStorePreferenceManager.notice

        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            setupNotificationsSwitch(
                dataStorePreferenceManager,
                isChecked,
                container.context
            )
        }

        binding.btnSignUp.setOnClickListener {
            switchToSignUpFragment()
        }

        binding.btnRegistration.setOnClickListener {
            loginFirebase()
        }
        return binding.root
    }

    private fun loginFirebase() {
        binding.apply {
            val email = etEmail.text?.trim().toString()
            val password = etPassword.text?.trim().toString()
            if (handleLoginValidation(email, password, binding, requireContext())) {
                viewModel.login(email, password)
                viewLifecycleOwner.lifecycleScope.launch {
                    loginFlow?.collect { resource ->
                        handleUILoginState(resource)
                    }
                }
            }
        }
    }

    private fun handleUILoginState(resource: Resource<FirebaseUser>?) {
        when (resource) {
            Resource.Loading -> {
                binding.apply {
                    progressBarSignIn.visibility = View.VISIBLE
                    btnRegistration.visibility = View.GONE
                    btnSignUp.visibility = View.GONE
                }
            }

            is Resource.Failure -> {
                Toast.makeText(context, resource.exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
                binding.apply {
                    progressBarSignIn.visibility = View.GONE
                    btnRegistration.visibility = View.VISIBLE
                    btnSignUp.visibility = View.VISIBLE
                }
            }

            is Resource.Success -> {
                switchToSettingsFragment()
            }

            else -> {
                Toast.makeText(
                    context,
                    getString(R.string.something_went_wrong),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun switchToSignUpFragment() {
        val signUpFragment = SignUpFragment.newInstance()
        parentFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, signUpFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun switchToSettingsFragment() {
        val settingsFragment = SettingsFragment.newInstance()
        parentFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, settingsFragment)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignInFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}