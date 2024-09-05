package com.danilovfa.core.library.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import kotlinx.coroutines.flow.first
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

@OptIn(ExperimentalSerializationApi::class)
@Suppress("BlockingMethodInNonBlockingContext", "FunctionName")
inline fun <reified T : Any> DataStoreSerializer(): Serializer<T?> {
    return object : Serializer<T?> {
        override val defaultValue: T? = null

        override suspend fun readFrom(input: InputStream): T? {
            return if (input.isNotEmpty()) {
                Json.decodeFromStream(input)
            } else {
                null
            }
        }

        override suspend fun writeTo(t: T?, output: OutputStream) {
            if (t != null) Json.encodeToStream(t, output)
        }

        private fun InputStream.isNotEmpty() = available() > 0
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Suppress("BlockingMethodInNonBlockingContext", "FunctionName")
inline fun <reified T : Any> DataStoreSerializer(defaultValue: T): Serializer<T> {
    return object : Serializer<T> {
        override val defaultValue: T = defaultValue

        override suspend fun readFrom(input: InputStream): T {
            return if (input.isNotEmpty()) {
                Json.decodeFromStream(input)
            } else {
                defaultValue
            }
        }

        override suspend fun writeTo(t: T, output: OutputStream) {
            Json.encodeToStream(t, output)
        }

        private fun InputStream.isNotEmpty() = available() > 0
    }
}

suspend fun <T : Any> DataStore<T?>.clear(): T? = updateData { null }
suspend fun <T : Any> DataStore<T?>.requireValue(): T = checkNotNull(data.first())
