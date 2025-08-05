package com.artemklymenko.cards.vm

import com.artemklymenko.cards.firestore.model.Resource
import com.artemklymenko.cards.firestore.repository.impl.FakeAuthRepository
import com.artemklymenko.cards.utils.MainDispatcherRule
import com.google.firebase.auth.FirebaseUser
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val testUser = mockk<FirebaseUser>(relaxed = true)
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: LoginViewModel

    companion object {
       private const val USER_EMAIL = "test@example.com"
       private const val USER_PASSWORD = "strongPassword123"
       private const val LOGIN_FAILED = "Login failed"
    }

    @Before
    fun setup() {
        fakeAuthRepository = FakeAuthRepository()
        viewModel = LoginViewModel(fakeAuthRepository)
    }

    @Test
    fun `login success updates loginFlow with Success`() = runTest {
        fakeAuthRepository.loginResult = Resource.Success(testUser)
        viewModel.login(USER_EMAIL, USER_PASSWORD)
        advanceUntilIdle()
        assertEquals(Resource.Success(testUser), viewModel.loginFlow.value)
    }

    @Test
    fun `login failure updates loginFlow with Failure`() = runTest {
        val exception = Exception(LOGIN_FAILED)
        fakeAuthRepository.loginResult = Resource.Failure(exception)

        viewModel.login(USER_EMAIL, USER_PASSWORD)
        advanceUntilIdle()

        val result = viewModel.loginFlow.value
        assertTrue(result is Resource.Failure)
        assertEquals(LOGIN_FAILED, (result as Resource.Failure).exception.message)
    }

    @Test
    fun `signup success updates signupFlow with Success`() = runTest {
        fakeAuthRepository.signupResult = Resource.Success(testUser)

        viewModel.signup(USER_EMAIL, USER_PASSWORD)
        advanceUntilIdle()

        val result = viewModel.signupFlow.value
        assertEquals(testUser, (result as Resource.Success).result)
    }

    @Test
    fun `logout clears login and signup flow`() = runTest {
        fakeAuthRepository.loginResult = Resource.Success(testUser)
        fakeAuthRepository.signupResult = Resource.Success(testUser)

        viewModel.login(USER_EMAIL, USER_PASSWORD)
        viewModel.signup(USER_EMAIL, USER_PASSWORD)
        advanceUntilIdle()

        viewModel.logout()

        assertNull(viewModel.loginFlow.value)
        assertNull(viewModel.signupFlow.value)
    }
}