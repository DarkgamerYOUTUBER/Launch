package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LauncherQuestDao {
    @Query("SELECT * FROM launcher_quests ORDER BY timestamp DESC")
    fun getAllQuests(): Flow<List<LauncherQuest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: LauncherQuest)

    @Update
    suspend fun updateQuest(quest: LauncherQuest)

    @Delete
    suspend fun deleteQuest(quest: LauncherQuest)

    @Query("DELETE FROM launcher_quests WHERE id = :id")
    suspend fun deleteQuestById(id: Int)
}

@Dao
interface UnlockedCardDao {
    @Query("SELECT * FROM unlocked_cards ORDER BY unlockedAt DESC")
    fun getAllUnlockedCards(): Flow<List<UnlockedCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: UnlockedCard)

    @Query("DELETE FROM unlocked_cards")
    suspend fun deleteAllCards()
}

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    fun getUserStatsFlow(): Flow<UserStatsRow?>

    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    suspend fun getUserStatsDirect(): UserStatsRow?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: UserStatsRow)
}
