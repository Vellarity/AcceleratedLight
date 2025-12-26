package com.vellarity.lightaccs.ui.screen

data class MainScreenState(
    val isLight: Boolean,
    val isShakeOn: Boolean,
    val shakeStrength: Float = 0.0f,
)