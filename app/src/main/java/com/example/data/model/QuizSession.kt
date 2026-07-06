package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_sessions")
data class QuizSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String, // "Physics", "Chemistry", "Mathematics"
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String, // "A", "B", "C", "D"
    val userSelectedAnswer: String? = null,
    val explanation: String,
    val timestamp: Long = System.currentTimeMillis()
)
