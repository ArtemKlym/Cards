package com.artemklymenko.cards.domain.validation

import android.content.Context
import com.artemklymenko.cards.R
import com.artemklymenko.cards.databinding.FragmentSignUpBinding
import com.artemklymenko.cards.domain.model.RegisterInputValidationType
import com.artemklymenko.cards.domain.use_case.ValidateRegisterUseCase

fun handleRegisterValidation(
    email: String,
    password: String,
    confirmedPassword: String,
    binding: FragmentSignUpBinding,
    context: Context
): Boolean{
    val validationType = ValidateRegisterUseCase().invoke(email,password,confirmedPassword)
    return when(validationType){
        RegisterInputValidationType.Valid -> {
            binding.apply {
                tilSignUpEmail.error = null
                tilSignUpPassword.error = null
                tilSignUpConfirmPassword.error = null
            }
            true
        }
            else -> {
                displayRegisterErrorMessage(validationType, binding, context)
                false
            }
        }
    }

fun displayRegisterErrorMessage(
    validationType: RegisterInputValidationType,
    binding: FragmentSignUpBinding,
    context: Context
) {
    val emailError = when(validationType){
        RegisterInputValidationType.EmptyField -> context.getString(R.string.email_can_t_be_empty)
        RegisterInputValidationType.NoEmail -> context.getString(R.string.please_enter_a_valid_email_address)
        RegisterInputValidationType.EmailIsTooShort -> context.getString(R.string.the_email_is_too_short)
        else -> context.getString(R.string.incorrect_credentials)
    }

    val passwordError = when(validationType){
        RegisterInputValidationType.EmptyField -> context.getString(R.string.password_can_t_be_empty)
        RegisterInputValidationType.PasswordIsTooShort -> context.getString(R.string.the_password_is_too_short)
        RegisterInputValidationType.PasswordsDoNotMatch -> context.getString(R.string.passwords_don_t_match)
        RegisterInputValidationType.PasswordPasswordSpecialCharMissing -> context.getString(R.string.the_password_must_contain_at_least_1_special_char)
        RegisterInputValidationType.PasswordUpperCaseMissing -> context.getString(R.string.the_password_must_contain_at_least_1_uppercase_letter)
        RegisterInputValidationType.PasswordNumberMissing -> context.getString(R.string.the_password_must_contain_at_least_1_number)
        else -> context.getString(R.string.incorrect_credentials)
    }

    val confirmedPasswordError = when(validationType){
        RegisterInputValidationType.EmptyField -> context.getString(R.string.the_confirmed_password_can_t_be_empty)
        RegisterInputValidationType.PasswordsDoNotMatch -> context.getString(R.string.passwords_don_t_match)
        else -> context.getString(R.string.incorrect_credentials)
    }

    binding.apply {
        tilSignUpEmail.error = emailError
        tilSignUpPassword.error = passwordError
        tilSignUpConfirmPassword.error = confirmedPasswordError
    }
}