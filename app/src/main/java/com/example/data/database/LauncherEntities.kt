package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "launcher_quests")
data class LauncherQuest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val gemsReward: Int = 15,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "unlocked_cards")
data class UnlockedCard(
    @PrimaryKey val id: String,
    val cardName: String,
    val characterTheme: String, // "Sakura", "Cyber", "Knight"
    val rarity: String,         // "R", "SR", "SSR", "UR"
    val level: Int = 1,
    val unlockedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_stats")
data class UserStatsRow(
    @PrimaryKey val id: Int = 1,
    val gems: Int = 100, // Starts with some gems
    val activeTheme: String = "Sakura",
    val affinitySakura: Int = 10,
    val affinityCyber: Int = 10,
    val affinityKnight: Int = 10,
    val totalQuestsCompleted: Int = 0,
    val totalGachaPulls: Int = 0
)
