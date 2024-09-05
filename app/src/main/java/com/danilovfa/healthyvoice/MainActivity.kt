package com.danilovfa.healthyvoice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.danilovfa.feature.root.DefaultRootComponent
import com.danilovfa.feature.root.RootScreen
import com.danilovfa.uikit.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val rootComponent = DefaultRootComponent(
            storeFactory = DefaultStoreFactory(),
            componentContext = defaultComponentContext()
        )

        setContent {
            AppTheme {
                RootScreen(component = rootComponent)
            }
        }
    }
}