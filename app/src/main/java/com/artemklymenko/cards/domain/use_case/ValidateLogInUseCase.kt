package com.artemklymenko.cards.domain.use_case

import com.artemklymenko.cards.domain.model.LoginInputValidationType
import com.artemklymenko.cards.utils.StringExtension

class ValidateLogInUseCase {

    operator fun invoke(email: String, password: String) : LoginInputValidationType{
        if(email.isEmpty() || password.isEmpty()){
            return LoginInputValidationType.EmptyField
        }
        if(!StringExtension().checkEmail(email)){
            return LoginInputValidationType.NoEmail
        }
        return LoginInputValidationType.Valid
    }
}