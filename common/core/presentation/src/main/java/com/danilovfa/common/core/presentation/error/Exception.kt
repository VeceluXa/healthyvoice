package com.danilovfa.common.core.presentation.error

import com.danilovfa.common.core.presentation.Text
import com.danilovfa.common.resources.strings
import com.danilovfa.core.presentation.error.Displayable
import java.net.ConnectException
import java.net.ProtocolException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable?.getErrorText(): Text {
    return when {
        this == null -> null
        this.isNetworkError() -> Text.Resource(strings.error_server)
        this is Displayable -> text
        else -> null
    } ?: Text.Resource(strings.something_went_wrong)
}

fun Throwable?.getErrorDialogTitle(): Text {
    return when {
        this == null -> null
        this.isNetworkError() -> Text.Resource(strings.network_error_title)
        this is Displayable -> this.getErrorText()
        else -> null
    } ?: Text.Resource(strings.network_error_title)
}

fun Throwable?.getErrorDialogDescription(): Text {
    return when {
        this == null -> null
        this.isNetworkError() -> Text.Resource(strings.network_error_description)
        this is Displayable -> description
        else -> null
    } ?: Text.Resource(strings.network_error_description)
}

fun Throwable.isNetworkError() = when (this) {
    is UnknownHostException,
    is ConnectException,
    is ProtocolException,
    is SocketTimeoutException -> true
    else -> false
}