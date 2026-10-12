package me.weishu.kernelsu.ui

import androidx.compose.runtime.staticCompositionLocalOf

enum class UiMode(val value: String) {
    Miuix("miuix"),
    Material("material"),
    Fluent("fluent");

    companion object {
        fun fromValue(value: String): UiMode = when (value) {
            Material.value -> Material
            Fluent.value -> Fluent
            else -> Miuix
        }

        val DEFAULT_VALUE = Miuix.value
    }
}

val LocalUiMode = staticCompositionLocalOf { UiMode.Miuix }
