package com.danilovfa.common.core.domain.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TextError(
    @SerialName("error")
    val error: String,
    @SerialName("status")
    val status: Int?
) {
    override fun toString(): String {
        return error
    }
}