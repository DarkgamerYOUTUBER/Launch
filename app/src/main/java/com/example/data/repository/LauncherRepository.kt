package com.example.data.repository

import com.example.data.database.LauncherQuest
import com.example.data.database.LauncherQuestDao
import com.example.data.database.UnlockedCard
import com.example.data.database.UnlockedCardDao
import com.example.data.database.UserStatsDao
import com.example.data.database.UserStatsRow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LauncherRepository(
    private val questDao: LauncherQuestDao,
    private val cardDao: UnlockedCardDao,
    private val statsDao: UserStatsDao
) {
    val allQuests: Flow<List<LauncherQuest>> = questDao.getAllQuests()
    val allUnlockedCards: Flow<List<UnlockedCard>> = cardDao.getAllUnlockedCards()
    
    // Ensure we always have default stats if none exist yet
    val userStats: Flow<UserStatsRow> = statsDao.getUserStatsFlow().map {
        it ?: UserStatsRow()
    }

    suspend fun insertQuest(quest: LauncherQuest) {
        questDao.insertQuest(quest)
    }

    suspend fun updateQuest(quest: LauncherQuest) {
        questDao.updateQuest(quest)
    }

    suspend fun deleteQuest(quest: LauncherQuest) {
        questDao.deleteQuest(quest)
    }

    suspend fun deleteQuestById(id: Int) {
        questDao.deleteQuestById(id)
    }

    suspend fun unlockCard(card: UnlockedCard) {
        cardDao.insertCard(card)
    }

    suspend fun clearAllCards() {
        cardDao.deleteAllCards()
    }

    suspend fun getOrCreateStatsDirect(): UserStatsRow {
        return statsDao.getUserStatsDirect() ?: UserStatsRow().also {
            statsDao.insertOrUpdateStats(it)
        }
    }

    suspend fun updateActiveTheme(themeName: String) {
        val current = getOrCreateStatsDirect()
        statsDao.insertOrUpdateStats(current.copy(activeTheme = themeName))
    }

    suspend fun addGems(amount: Int) {
        val current = getOrCreateStatsDirect()
        statsDao.insertOrUpdateStats(current.copy(gems = current.gems + amount))
    }

    suspend fun spendGems(amount: Int): Boolean {
        val current = getOrCreateStatsDirect()
        if (current.gems < amount) return false
        statsDao.insertOrUpdateStats(current.copy(gems = current.gems - amount))
        return true
    }

    suspend fun incrementGachaPulls() {
        val current = getOrCreateStatsDirect()
        statsDao.insertOrUpdateStats(current.copy(totalGachaPulls = current.totalGachaPulls + 1))
    }

    suspend fun incrementQuestsCompleted() {
        val current = getOrCreateStatsDirect()
        statsDao.insertOrUpdateStats(current.copy(totalQuestsCompleted = current.totalQuestsCompleted + 1))
    }

    suspend fun increaseAffinity(theme: String, amount: Int) {
        val current = getOrCreateStatsDirect()
        val updated = when (theme.lowercase()) {
            "sakura" -> current.copy(affinitySakura = current.affinitySakura + amount)
            "cyber" -> current.copy(affinityCyber = current.affinityCyber + amount)
            "knight" -> current.copy(affinityKnight = current.affinityKnight + amount)
            else -> current
        }
        statsDao.insertOrUpdateStats(updated)
    }
}
