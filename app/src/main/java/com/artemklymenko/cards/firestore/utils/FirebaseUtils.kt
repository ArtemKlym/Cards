package com.artemklymenko.cards.firestore.utils

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

/**
 * The function to get the result from the task without using callbacks
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Task<T>.await(): T{
    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            if(it.exception != null){
                cont.resumeWithException(it.exception!!)
            }else{
                cont.resume(it.result, null)
            }
        }
    }
}