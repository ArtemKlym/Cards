package com.artemklymenko.cards.utils

import com.artemklymenko.cards.domain.model.RegisterInputValidationType
import com.artemklymenko.cards.domain.use_case.ValidateRegisterUseCase
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StringExtensionTest {

    private val validateRegisterUseCase = ValidateRegisterUseCase()

    @Test
    fun `test empty email field return validation type empty field`() {
        val result = validateRegisterUseCase.invoke(
            email = "",
            password = "Password+1",
            passwordRepeated = "Password+1"
        )
        assertThat(result).isEqualTo(RegisterInputValidationType.EmptyField)
    }

    @Test
    fun `test empty password field return validation type empty field`() {
        val result = validateRegisterUseCase.invoke(
            email = "user1@test.com",
            password = "",
            passwordRepeated = "Password+1"
        )
        assertThat(result).isEqualTo(RegisterInputValidationType.EmptyField)
    }

    @Test
    fun `test empty repeated password field return validation type empty field`() {
        val result = validateRegisterUseCase.invoke(
            email = "user1@test.com",
            password = "Password+1",
            passwordRepeated = ""
        )
        assertThat(result).isEqualTo(RegisterInputValidationType.EmptyField)
    }

    @Test
    fun `test email without '@' return validation type no email`() {
        val result = validateRegisterUseCase.invoke(
            email = "user12test.com",
            password = "Password+1",
            passwordRepeated = "Password+1",
        )
        assertThat(result).isEqualTo(RegisterInputValidationType.NoEmail)
    }

    @Test
    fun `test email without dot return validation type no email`() {
        val result = validateRegisterUseCase.invoke(
            email = "user12@testcom",
            password = "Password+1",
            passwordRepeated = "Password+1"
        )
        assertThat(result).isEqualTo(RegisterInputValidationType.NoEmail)
    }

    @Test
    fun `test email with a first character as a number return validation type no email`() {
        val result = validateRegisterUseCase.invoke(
            email = "1user@testcom",
            password = "Password+1",
            passwordRepeated = "Password+1"
        )
        assertThat(result).isEqualTo(RegisterInputValidationType.NoEmail)
    }

    @Test
    fun `test length of the email`() {
        val result = validateRegisterUseCase.invoke(
            email = "u@t.c",
            password = "Password+1",
            passwordRepeated = "Password+1"
        )
        assertThat(result).isEqualTo(RegisterInputValidationType.EmailIsTooShort)
    }

    @Test
    fun `test length of the password`() {
        val result = validateRegisterUseCase.invoke(
            email = "user1@test.com",
            password = "Pass+1",
            passwordRepeated = "Pass+1"
        )
        assertThat(result).isEqualTo(RegisterInputValidationType.PasswordIsTooShort)
    }

    @Test
    fun `test string contains no character returns false when check for it`() {
        val result = validateRegisterUseCase.invoke(
            email = "user1@test.com",
            password = "Password1",
            passwordRepeated = "Password1"
        )
        assertThat(result).isEqualTo(RegisterInputValidationType.PasswordPasswordSpecialCharMissing)
    }

    @Test
    fun `test string contains no number returns false when check for it`() {
        val result = validateRegisterUseCase.invoke(
            email = "user1@test.com",
            password = "Password+",
            passwordRepeated = "Password+"
        )
        assertThat(result).isEqualTo(RegisterInputValidationType.PasswordNumberMissing)
    }

    @Test
    fun `test string contains no upper case return false when check for it`() {
        val result = validateRegisterUseCase.invoke(
            email = "user1@test.com",
            password = "password+1",
            passwordRepeated = "password+1"
        )
        assertThat(result).isEqualTo(RegisterInputValidationType.PasswordUpperCaseMissing)
    }

    @Test
    fun `test if passwords matches`() {
        val result = validateRegisterUseCase.invoke(
            email = "user1@test.com",
            password = "Password+1",
            passwordRepeated = "Passw0rd+2"
        )
        assertThat(result).isEqualTo(RegisterInputValidationType.PasswordsDoNotMatch)
    }
}