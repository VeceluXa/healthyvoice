package com.danilovfa.core.library.exception

import com.danilovfa.core.library.text.Text
import com.danilovfa.resources.R
import java.net.ConnectException
import java.net.ProtocolException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable?.getErrorText(): Text {
    return when {
        this == null -> null
        this.isNetworkError() -> Text.Resource(R.string.error_server)
        this is Displayable -> text
        else -> null
    } ?: Text.Resource(R.string.something_went_wrong)
}

fun Throwable?.getErrorDialogTitle(): Text {
    return when {
        this == null -> null
        this.isNetworkError() -> Text.Resource(R.string.network_error_title)
        this is Displayable -> this.getErrorText()
        else -> null
    } ?: Text.Resource(R.string.network_error_title)
}

fun Throwable?.getErrorDialogDescription(): Text {
    return when {
        this == null -> null
        this.isNetworkError() -> Text.Resource(R.string.network_error_description)
        this is Displayable -> description
        else -> null
    } ?: Text.Resource(R.string.network_error_description)
}

fun Throwable.isNetworkError() = when (this) {
    is UnknownHostException,
    is ConnectException,
    is ProtocolException,
    is SocketTimeoutException -> true
    else -> false
}