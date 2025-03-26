package com.danilovfa.common.uikit.event

import android.graphics.Bitmap
import androidx.annotation.StringRes
import com.danilovfa.common.base.event.Event
import com.danilovfa.common.base.event.EventDelegate
import com.danilovfa.common.core.domain.network.ApiResponse
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.common.core.presentation.error.getErrorText
import com.danilovfa.common.resources.strings
import com.danilovfa.common.uikit.composables.snackbar.SnackbarStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

internal data class MessageEvent(val message: Text, val snackbarStyle: SnackbarStyle) : Event
internal data class ImageMessageEvent(val image: Bitmap, val message: Text) : Event
internal data class InterstitialMessageEvent(val message: Text, val snackbarStyle: SnackbarStyle) :
    Event
internal data class ErrorMessageEvent(val message: Text) : Event

fun EventDelegate.showMessage(
    message: String,
    snackbarStyle: SnackbarStyle = SnackbarStyle.Default,
) {
    offerEvent(MessageEvent(Text.Plain(message), snackbarStyle))
}

fun EventDelegate.showMessage(
    text: Text,
    snackbarStyle: SnackbarStyle = SnackbarStyle.Default,
) {
    offerEvent(MessageEvent(text, snackbarStyle))
}

fun EventDelegate.showImageMessage(
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

fun EventDelegate.showMessage(
    @StringRes resourceId: Int,
    snackbarStyle: SnackbarStyle = SnackbarStyle.Default,
) {
    offerEvent(MessageEvent(Text.Resource(resourceId), snackbarStyle))
}

fun <E> EventDelegate.showApiError(
    error: ApiResponse.Error<E>,
    transformError: (E?) -> Text = { errorValue ->
        errorValue?.toString()?.let { Text.Plain(it) } ?: Text.Resource(strings.error_server)
    }
) {
    val errorText = when (error) {
        is ApiResponse.Error.HttpError -> transformError(error.errorBody)
        ApiResponse.Error.NetworkError -> Text.Resource(strings.network_error_title)
        ApiResponse.Error.SerializationError -> Text.Resource(strings.error_server)
        ApiResponse.Error.UnknownException -> Text.Resource(strings.error_server)
    }

    offerEvent(ErrorMessageEvent(errorText))
}

fun EventDelegate.showError(message: String) {
    offerEvent(ErrorMessageEvent(Text.Plain(message)))
}

fun EventDelegate.showError(text: Text?) {
    val errorMessage = text ?: Text.Resource(strings.error_server)
    offerEvent(ErrorMessageEvent(errorMessage))
}

fun EventDelegate.showError(@StringRes resourceId: Int) {
    offerEvent(ErrorMessageEvent(Text.Resource(resourceId)))
}

fun EventDelegate.showError(error: Throwable?) {
    offerEvent(ErrorMessageEvent(error.getErrorText()))
}

private val todoMessages = listOf(
    Text.Resource(strings.todo_1),
    Text.Resource(strings.todo_2),
    Text.Resource(strings.todo_3)
)

/** Use it if functionality is not implemented yet. */
fun EventDelegate.showTodo() {
    offerEvent(MessageEvent(todoMessages.random(), SnackbarStyle.Default))
}