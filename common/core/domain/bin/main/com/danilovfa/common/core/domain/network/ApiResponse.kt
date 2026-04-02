package com.danilovfa.common.core.domain.network

data class ApiError(val error: ApiResponse.Error<*>) : Throwable()

sealed class ApiResponse<out T, out E> {

    /**
     * Represents successful network responses (2xx)
     */
    data class Success<T>(val body: T) : ApiResponse<T, Nothing>()

    sealed class Error<E> : ApiResponse<Nothing, E>() {
        /**
         * Represents server (50x) and client (40x) errors
         */
        data class HttpError<E>(val code: Int, val errorBody: E?) : Error<E>()

        /**
         * Represents IOExceptions and connectivity issues
         */
        data object NetworkError : Error<Nothing>()

        /**
         * Represents SerializationException, NoTransformationException
         */
        data object SerializationError : Error<Nothing>()

        data object UnknownException : Error<Nothing>()
    }
}

fun ApiResponse<*, *>.isSuccess(): Boolean = this is ApiResponse.Success
fun ApiResponse<*, *>.isError(): Boolean = isSuccess().not()

inline fun <reified T, reified E, reified S> ApiResponse<T, E>.map(
    transform: ApiResponse<T, E>.() -> ApiResponse<S, E>
): ApiResponse<S, E> = transform()

inline fun <reified T, reified E, reified S> ApiResponse<T, E>.mapSuccess(
    transform: (T) -> S
): ApiResponse<S, E> {
    return when (this) {
        is ApiResponse.Success -> ApiResponse.Success(transform(body))
        is ApiResponse.Error.HttpError -> ApiResponse.Error.HttpError(code, errorBody)
        ApiResponse.Error.NetworkError -> ApiResponse.Error.NetworkError
        ApiResponse.Error.SerializationError -> ApiResponse.Error.SerializationError
        ApiResponse.Error.UnknownException -> ApiResponse.Error.UnknownException
    }
}

inline fun <reified T, reified E, reified S> ApiResponse<T, E>.mapError(
    transform: (E?) -> S?
): ApiResponse<T, S> {
    return when (this) {
        is ApiResponse.Success -> this
        is ApiResponse.Error.HttpError -> ApiResponse.Error.HttpError(code, transform(errorBody))
        ApiResponse.Error.NetworkError -> ApiResponse.Error.NetworkError
        ApiResponse.Error.SerializationError -> ApiResponse.Error.SerializationError
        ApiResponse.Error.UnknownException -> ApiResponse.Error.UnknownException
    }
}

inline fun <reified T, reified E> ApiResponse<T, E>.onError(
    block: (ApiResponse.Error<E>) -> Unit
): ApiResponse<T, E> {
    if (this is ApiResponse.Error) block(this)
    return this
}

inline fun <reified T> ApiResponse<T, *>.valueOrNull(): T? =
    (this as? ApiResponse.Success)?.body

inline fun <reified T, reified E> ApiResponse<T, E>.onSuccess(
    block: (T) -> Unit
): ApiResponse<T, E> {
    if (this is ApiResponse.Success) block(body)
    return this
}