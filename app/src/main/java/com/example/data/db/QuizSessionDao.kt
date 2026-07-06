package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.QuizSession
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizSessionDao {
    @Query("SELECT * FROM quiz_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<QuizSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: QuizSession): Long

    @Update
    suspend fun updateSession(session: QuizSession)

    @Query("DELETE FROM quiz_sessions")
    suspend fun clearAllSessions()
}
