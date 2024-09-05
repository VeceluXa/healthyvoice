package com.danilovfa.core.library.decompose

import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackHandler

fun BackHandler.onBackClicked(callback: () -> Unit) {
    register(BackCallback(onBack = callback))
}