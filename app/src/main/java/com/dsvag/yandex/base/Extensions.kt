package com.dsvag.yandex.base

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
fun EditText.textChanges(): Flow<CharSequence?> = callbackFlow<CharSequence?> {
    val listener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            sendBlocking(s)
        }
    }

    addTextChangedListener(listener)

    awaitClose { removeTextChangedListener(listener) }

}.filter { it.isNotNull() }.onStart { emit(text) }

fun Any?.isNull(): Boolean = this == null

fun Any?.isNotNull(): Boolean = this != null

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun <T> Flow<T>.launchWhenCreated(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launchWhenCreated {
        this@launchWhenCreated.collect()
    }
}

fun <T> Flow<T>.launchWhenStarted(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launchWhenStarted {
        this@launchWhenStarted.collect()
    }
}

fun <T> Flow<T>.launchWhenResumed(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launchWhenResumed {
        this@launchWhenResumed.collect()
    }
}