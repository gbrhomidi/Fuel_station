package com.aistudio.dieselstationsms.kxmpzq.ui.state

/**
 * ═══════════════════════════════════════════════════════════════
 * Base UiState — Sealed Class لإدارة حالات الشاشة
 * ═══════════════════════════════════════════════════════════════
 */
sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

/**
 * حالة العمليات (Action States) — للكتابة/الحذف/التحديث
 */
sealed class ActionState {
    data object Idle : ActionState()
    data object Loading : ActionState()
    data class Success(val message: String = "") : ActionState()
    data class Error(val message: String) : ActionState()
}