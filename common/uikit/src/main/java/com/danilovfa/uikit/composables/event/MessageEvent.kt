package com.danilovfa.uikit.composables.event

import android.graphics.Bitmap
import androidx.annotation.StringRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import com.danilovfa.core.base.presentation.event.Event
import com.danilovfa.core.base.presentation.event.EventsDispatcher
import com.danilovfa.core.library.exception.getErrorText
import com.danilovfa.core.library.text.Text
import com.danilovfa.uikit.composables.snackbar.SnackbarStyle
import com.danilovfa.resources.R

internal data class MessageEvent(val message: Text, val snackbarStyle: SnackbarStyle) : Event
internal data class ImageMessageEvent(val image: Bitmap, val message: Text) : Event
internal data class InterstitialMessageEvent(val message: Text, val snackbarStyle: SnackbarStyle) :
    Event
internal data class ErrorMessageEvent(val message: Text) : Event

fun EventsDispatcher.showMessage(
    message: String,
    snackbarStyle: SnackbarStyle = SnackbarStyle.Default,
) {
    offerEvent(MessageEvent(Text.Plain(message), snackbarStyle))
}

fun EventsDispatcher.showMessage(
    text: Text,
    snackbarStyle: SnackbarStyle = SnackbarStyle.Default,
) {
    offerEvent(MessageEvent(text, snackbarStyle))
}

fun EventsDispatcher.showImageMessage(
    image: Bitmap,
    text: Text,
) {
    offerEvent(ImageMessageEvent(image, text))
}

fun MutableSharedFlow<Event>.showInterstitialMessage(
    text: Text,
    coroutineScope: CoroutineScope,
    snackbarStyle: SnackbarStyle = SnackbarStyle.Default,
) {
    coroutineScope.launch { emit(InterstitialMessageEvent(text, snackbarStyle)) }
}

fun MutableSharedFlow<Event>.showInterstitialMessage(
    message: String,
    coroutineScope: CoroutineScope,
    snackbarStyle: SnackbarStyle = SnackbarStyle.Default,
) {
    coroutineScope.launch { emit(InterstitialMessageEvent(Text.Plain(message), snackbarStyle)) }
}

fun MutableSharedFlow<Event>.showInterstitialMessage(
    messageRes: Int,
    coroutineScope: CoroutineScope,
    snackbarStyle: SnackbarStyle = SnackbarStyle.Default,
) {
    coroutineScope.launch { emit(InterstitialMessageEvent(Text.Resource(messageRes), snackbarStyle)) }
}

fun EventsDispatcher.showMessage(
    @StringRes resourceId: Int,
    snackbarStyle: SnackbarStyle = SnackbarStyle.Default,
) {
    offerEvent(MessageEvent(Text.Resource(resourceId), snackbarStyle))
}

fun EventsDispatcher.showError(message: String) {
    offerEvent(ErrorMessageEvent(Text.Plain(message)))
}

fun EventsDispatcher.showError(text: Text) {
    offerEvent(ErrorMessageEvent(text))
}

fun EventsDispatcher.showError(@StringRes resourceId: Int) {
    offerEvent(ErrorMessageEvent(Text.Resource(resourceId)))
}

fun EventsDispatcher.showError(error: Throwable?) {
    offerEvent(ErrorMessageEvent(error.getErrorText()))
}

private val todoMessages = listOf(
    Text.Resource(R.string.todo_1),
    Text.Resource(R.string.todo_2),
    Text.Resource(R.string.todo_3)
)

/** Use it if functionality is not implemented yet. */
fun EventsDispatcher.showTodo() {
    offerEvent(MessageEvent(todoMessages.random(), SnackbarStyle.Default))
}