package com.artemklymenko.cards.domain.validation

import android.content.Context
import com.artemklymenko.cards.R
import com.artemklymenko.cards.databinding.FragmentSignInBinding
import com.artemklymenko.cards.domain.model.LoginInputValidationType
import com.artemklymenko.cards.domain.use_case.ValidateLogInUseCase

fun handleLoginValidation(
    email: String,
    password: String,
    binding: FragmentSignInBinding,
    context: Context
): Boolean{
    val validationType = ValidateLogInUseCase().invoke(email, password)
    return when (validationType) {
        LoginInputValidationType.Valid -> {
            binding.apply {
                tilEmail.error = null
                tilPassword.error = null
            }
            true
        }
        else -> {
            displayErrorMessage(validationType, binding, context)
            false
        }
    }
}

private fun displayErrorMessage(
    validationType: LoginInputValidationType,
    binding: FragmentSignInBinding,
    context: Context
){
    val emailError = when(validationType){
        LoginInputValidationType.NoEmail -> context.getString(R.string.please_enter_a_valid_email_address)
        LoginInputValidationType.EmptyField -> context.getString(R.string.email_can_t_be_empty)
        else -> context.getString(R.string.incorrect_credentials)
    }

    val passwordError = when(validationType){
        LoginInputValidationType.EmptyField -> context.getString(R.string.password_can_t_be_empty)
        else -> context.getString(R.string.incorrect_credentials)
    }

    binding.apply {
        tilEmail.error = emailError
        tilPassword.error = passwordError
    }
}