package com.artemklymenko.cards.firestore.repository.impl

import com.artemklymenko.cards.firestore.model.Resource
import com.artemklymenko.cards.firestore.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser

class FakeAuthRepository : AuthRepository {

    var loginResult: Resource<FirebaseUser> = Resource.Failure(Exception("Not set"))
    var signupResult: Resource<FirebaseUser> = Resource.Failure(Exception("Not set"))
    private var fakeCurrentUser: FirebaseUser? = null

    override val currentUser: FirebaseUser?
        get() = fakeCurrentUser

    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return loginResult
    }

    override suspend fun signup(email: String, password: String): Resource<FirebaseUser> {
        return signupResult
    }

    override fun logout() {
        fakeCurrentUser = null
    }
}