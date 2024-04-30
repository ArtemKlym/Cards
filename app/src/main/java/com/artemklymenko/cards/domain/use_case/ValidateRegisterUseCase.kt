package com.artemklymenko.cards.domain.use_case

import com.artemklymenko.cards.domain.model.RegisterInputValidationType
import com.artemklymenko.cards.utils.StringExtension

class ValidateRegisterUseCase {

    operator fun invoke(
        email: String,
        password: String,
        passwordRepeated: String
    ): RegisterInputValidationType{

        if(email.isEmpty() || password.isEmpty() || passwordRepeated.isEmpty()){
            return RegisterInputValidationType.EmptyField
        }

        if(!StringExtension().checkEmail(email)){
            return RegisterInputValidationType.NoEmail
        }

        if(password != passwordRepeated){
            return RegisterInputValidationType.PasswordsDoNotMatch
        }

        if(password.length < 8){
            return RegisterInputValidationType.PasswordIsTooShort
        }

        if(email.length < 14){
            return RegisterInputValidationType.EmailIsTooShort
        }

        if(!StringExtension().containsSpecialChar(password)){
            return RegisterInputValidationType.PasswordPasswordSpecialCharMissing
        }

        if(!StringExtension().containsNumber(password)){
            return RegisterInputValidationType.PasswordNumberMissing
        }

        if(!StringExtension().containsUpperCase(password)){
            return RegisterInputValidationType.PasswordUpperCaseMissing
        }

        return RegisterInputValidationType.Valid
    }
}