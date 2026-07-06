package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.QuizSession
import com.example.data.repository.TutorRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TutorViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = TutorRepository(
        chatMessageDao = database.chatMessageDao(),
        quizSessionDao = database.quizSessionDao()
    )

    val chatMessages: StateFlow<List<ChatMessage>> = repository.allMessages.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val quizSessions: StateFlow<List<QuizSession>> = repository.allQuizSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    var isChatLoading by mutableStateOf(false)
        private set

    var isQuizLoading by mutableStateOf(false)
        private set

    var currentQuiz by mutableStateOf<QuizSession?>(null)
        private set

    var quizStatusMessage by mutableStateOf("")
        private set

    var activeTab by mutableStateOf("chat") // "chat", "quiz", "formulas"

    init {
        // Inject a welcoming message if chat is empty
        viewModelScope.launch {
            chatMessages.collect { list ->
                if (list.isEmpty()) {
                    val welcomeMsg = ChatMessage(
                        sender = "tutor",
                        text = "Namaste! Main hoon aapki personal AI Tutor Ananya. Main aapko JEE Physics, Chemistry, aur Maths preps me guide karungi. Aap kisi bhi topic ka conceptual doubt puch sakte hain, ya 'Practice Quiz' tab me mere banaye JEE-level questions solve kar sakte hain! Let's crack JEE together! 🚀"
                    )
                    database.chatMessageDao().insertMessage(welcomeMsg)
                }
            }
        }
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            isChatLoading = true
            repository.sendMessage(text)
            isChatLoading = false
        }
    }

    fun startNewQuiz(subject: String) {
        viewModelScope.launch {
            isQuizLoading = true
            currentQuiz = null
            quizStatusMessage = ""
            val quiz = repository.generateQuizQuestion(subject)
            if (quiz != null) {
                currentQuiz = quiz
            } else {
                quizStatusMessage = "Kuch network error ki wajah se quiz question load nahi ho paya. Please dobara try kijiye!"
            }
            isQuizLoading = false
        }
    }

    fun submitQuizOption(option: String) {
        val quiz = currentQuiz ?: return
        if (quiz.userSelectedAnswer != null) return // Already answered
        
        viewModelScope.launch {
            repository.submitQuizAnswer(quiz, option)
            currentQuiz = quiz.copy(userSelectedAnswer = option)
            
            // Also post a comment from Ananya in chat history if they want to refer to it!
            val feedback = if (option == quiz.correctAnswer) {
                "Sahi jawab! 🎉 Correct Answer is $option. Bahut badiya concept clearance hai aapka!"
            } else {
                "Galat jawab! 😅 Correct Answer is ${quiz.correctAnswer}. No problem, let's learn from this explanation!"
            }
            quizStatusMessage = feedback
        }
    }

    fun resetHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            currentQuiz = null
            quizStatusMessage = ""
            
            // Re-insert welcome
            val welcomeMsg = ChatMessage(
                sender = "tutor",
                text = "Namaste! Main hoon aapki personal AI Tutor Ananya. Main aapko JEE Physics, Chemistry, aur Maths preps me guide karungi. Aap kisi bhi topic ka conceptual doubt puch sakte hain, ya 'Practice Quiz' tab me mere banaye JEE-level questions solve kar sakte hain! Let's crack JEE together! 🚀"
            )
            database.chatMessageDao().insertMessage(welcomeMsg)
        }
    }
}
