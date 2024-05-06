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
import com.artemklymenko.cards.data.Resource
import com.artemklymenko.cards.databinding.FragmentSignUpBinding
import com.artemklymenko.cards.domain.validation.handleRegisterValidation
import com.artemklymenko.cards.vm.LoginViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LoginViewModel
    private var signupFlow: StateFlow<Resource<FirebaseUser>?>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]
        signupFlow = viewModel.signupFlow
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding.btnSignIn.setOnClickListener {
            switchBackToSignInFragment()
        }

        binding.apply {
            btnSignUp.setOnClickListener {
                signUpFirebase()
                viewLifecycleOwner.lifecycleScope.launch {
                    signupFlow?.collect { resource ->
                        handleUISignupState(resource)
                    }
                }
            }
        }
        return binding.root
    }

    private fun signUpFirebase() {
        binding.apply {
            val email = etSignUpEmail.text?.trim().toString()
            val password = etSignUpPassword.text?.trim().toString()
            val confirmPassword = etSignUpConfirmPassword.text?.trim().toString()
            val result = handleRegisterValidation(
                email,
                password,
                confirmPassword,
                binding,
                requireContext()
            )
            if (result) {
                viewModel.signup(email, password)
            }
        }
    }

    private fun handleUISignupState(resource: Resource<FirebaseUser>?) {
        when (resource) {
            Resource.Loading -> {
                binding.apply {
                    progressBarSignUp.visibility = View.VISIBLE
                    btnSignUp.visibility = View.GONE
                }
            }

            is Resource.Failure -> {
                Toast.makeText(context, resource.exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
                binding.apply {
                    progressBarSignUp.visibility = View.GONE
                    btnSignUp.visibility = View.VISIBLE
                }
            }

            is Resource.Success -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.account_has_been_created),
                    Toast.LENGTH_SHORT
                ).show()
                switchBackToSignInFragment()
            }

            else -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun switchBackToSignInFragment() {
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignUpFragment()
    }
}