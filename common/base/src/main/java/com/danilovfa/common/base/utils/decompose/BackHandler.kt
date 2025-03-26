package com.danilovfa.common.base.utils.decompose

import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackHandler

fun BackHandler.onBackClicked(callback: () -> Unit) {
    register(BackCallback(onBack = callback))
}