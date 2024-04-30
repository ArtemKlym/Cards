package com.artemklymenko.cards.domain.model

enum class RegisterInputValidationType {
    EmptyField,
    NoEmail,
    PasswordsDoNotMatch,
    PasswordUpperCaseMissing,
    PasswordNumberMissing,
    PasswordPasswordSpecialCharMissing,
    PasswordIsTooShort,
    EmailIsTooShort,
    Valid
}