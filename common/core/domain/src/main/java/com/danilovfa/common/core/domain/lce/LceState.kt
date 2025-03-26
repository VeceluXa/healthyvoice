package com.danilovfa.common.core.domain.lce

import com.danilovfa.common.core.domain.lce.LceState.Content
import com.danilovfa.common.core.domain.lce.LceState.Error
import com.danilovfa.common.core.domain.lce.LceState.Loading
import kotlinx.coroutines.flow.flowOf
import kotlin.Boolean
import kotlin.Suppress
import kotlin.Throwable
import kotlin.Unit

/**
 * [Lce] no content.
 * Used to describe the [Loading], [Content] and [Error] states.
 * @see Lce.ignoreContent
 */
sealed interface LceState {

    val isLoading: Boolean
    val isContent: Boolean
    val isError: Boolean

    fun errorOrNull(): Throwable?

    sealed interface Loading : LceState

    sealed interface Content : LceState

    sealed interface Error : LceState {
        val error: Throwable
    }

    companion object {
        val Loading: Loading = Lce.Loading
        val Content: Content = Lce.Content(Unit)

        @Suppress("FunctionNaming")
        fun Error(error: Throwable): Error = Lce.Error(error)
    }
}

/** Turns [Lce] into [LceState] ignoring the contents of the [Lce.Content] state. */
fun Lce<*>.ignoreContent(): LceState {
    return when (this) {
        is Lce.Loading -> this
        is Lce.Error -> this
        is Lce.Content -> Lce.Content(Unit)
    }
}

/** Returns a different value depending on the [LceState] state. */
inline fun <R> LceState.fold(onLoading: () -> R, onError: (Throwable) -> R, onContent: () -> R): R {
    return when (this) {
        is Loading -> onLoading()
        is Error -> onError(error)
        is Content -> onContent()
    }
}

inline fun <T> LceState.fillContent(contentProducer: () -> T): Lce<T> {
    return fold(
        onLoading = { Lce.Loading },
        onError = { Lce.Error(it) },
        onContent = { Lce.of(contentProducer.invoke()) }
    )
}

inline fun <T> LceState.flatMap(contentFlowProducer: () -> LceFlow<T>): LceFlow<T> {
    return fold(
        onLoading = { flowOf(Lce.Loading) },
        onError = { flowOf(Lce.Error(it)) },
        onContent = { contentFlowProducer.invoke() }
    )
}
