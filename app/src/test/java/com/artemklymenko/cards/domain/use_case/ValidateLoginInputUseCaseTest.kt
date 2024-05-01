package com.artemklymenko.cards.domain.use_case

import com.artemklymenko.cards.domain.model.LoginInputValidationType
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ValidateLoginInputUseCaseTest {

    private val validateLogInUseCase = ValidateLogInUseCase()

    @Test
    fun `test empty email field return validation type empty field`() {
        val result = validateLogInUseCase.invoke(email = "", password = "password")
        assertThat(result).isEqualTo(LoginInputValidationType.EmptyField)
    }

    @Test
    fun `test empty password field return validation type empty field`() {
        val result = validateLogInUseCase.invoke(email = "user@test.com", password = "")
        assertThat(result).isEqualTo(LoginInputValidationType.EmptyField)
    }

    @Test
    fun `test is email valid without '@' return validation type no email`() {
        val result = validateLogInUseCase.invoke(email = "usertest.com", password = "password")
        assertThat(result).isEqualTo(LoginInputValidationType.NoEmail)
    }

    @Test
    fun `test is email valid without 'dot' return validation type no email`() {
        val result = validateLogInUseCase.invoke(email = "user@testcom", password = "password")
        assertThat(result).isEqualTo(LoginInputValidationType.NoEmail)
    }

    @Test
    fun `test is everything is correct return validation type valid`() {
        val result = validateLogInUseCase.invoke(email = "user@test.com", password = "password")
        assertThat(result).isEqualTo(LoginInputValidationType.Valid)
    }
}