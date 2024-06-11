package com.artemklymenko.cards.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artemklymenko.cards.db.Words
import com.artemklymenko.cards.firestore.model.Response
import com.artemklymenko.cards.firestore.repository.impl.CardsRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirestoreViewModel @Inject constructor(
    private val repositoryImpl: CardsRepositoryImpl
) : ViewModel() {

    private val _addCardResult = MutableLiveData<Response<String>>()
    val addCardResult = _addCardResult

    private val _deleteCardResult = MutableLiveData<Response<Boolean>>()
    val deleteCardResult = _deleteCardResult

    private val _updateCardResult = MutableLiveData<Response<String?>>()
    val updateResultCard = _updateCardResult

    fun addCardToFirestore(userId: String, card: Words) {
        viewModelScope.launch {
            val firestoreResult = repositoryImpl.addCardToFirestore(userId, card)
            if (firestoreResult is Response.Success) {
                _addCardResult.value = firestoreResult
            } else if (firestoreResult is Response.Failure) {
                _addCardResult.value = firestoreResult
            }
        }
    }

    fun deleteCardFromFirestore(userId: String, sid: String?) {
        viewModelScope.launch {
            val firestoreResult = repositoryImpl.deleteCardFromFirestore(userId, sid)
            if (firestoreResult is Response.Success) {
                _deleteCardResult.value = firestoreResult
            } else if (firestoreResult is Response.Failure) {
                _deleteCardResult.value = firestoreResult
            }
        }
    }

    fun updateCardFromFirestore(userId: String, sid: String?, words: Words) {
        viewModelScope.launch {
            val firestoreResult = repositoryImpl.updateCardInFirestore(userId, sid, words)
            if (firestoreResult is Response.Success) {
                _updateCardResult.value = firestoreResult
            } else {
                _updateCardResult.value = firestoreResult
            }
        }
    }
}