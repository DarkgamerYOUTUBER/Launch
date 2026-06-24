package com.example.data.model

import android.content.Intent

data class LauncherApp(
    val packageName: String,
    val label: String,
    val isVirtual: Boolean,
    val customEmoji: String,
    val category: String, // "Utility", "Social", "Media", "Games", "Anime"
    val launchIntent: Intent? = null,
    val description: String = ""
)
