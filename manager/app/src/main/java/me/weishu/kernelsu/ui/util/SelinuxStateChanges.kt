package me.weishu.kernelsu.ui.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object SelinuxStateChanges {
    private val revision = MutableStateFlow(0L)
    val changes = revision.asStateFlow()

    fun notifyChanged() {
        revision.update { it + 1 }
    }
}
