package com.example.data.repository

import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.GenerationConfig
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import com.example.data.api.SystemInstruction
import com.example.data.db.ChatMessageDao
import com.example.data.db.QuizSessionDao
import com.example.data.model.ChatMessage
import com.example.data.model.QuizSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TutorRepository(
    private val chatMessageDao: ChatMessageDao,
    private val quizSessionDao: QuizSessionDao
) {
    val allMessages: Flow<List<ChatMessage>> = chatMessageDao.getAllMessages()
    val allQuizSessions: Flow<List<QuizSession>> = quizSessionDao.getAllSessions()

    private val systemPrompt = """
        You are Ananya, a highly intelligent, encouraging, and friendly AI Tutor specializing in JEE competitive exam preparation (Physics, Chemistry, and Mathematics).
        Your character profile:
        - You are a charismatic young female Indian professional teacher with a warm, friendly smile and encouraging vibe.
        - You speak primarily in Hinglish (a natural mix of Hindi and English, e.g., "Aapko thermodynamics ke standard formulas yaad rakhne chahiye", "Let's solve this projectile motion question together!"). This makes it easy and highly relatable for JEE aspirants.
        - You explain complex science, physics, and math concepts with clear steps, simplified analogies, and accurate math formulas.
        - You are extremely supportive and positive, boosting the student's motivation and confidence.
    """.trimIndent()

    suspend fun sendMessage(userText: String) = withContext(Dispatchers.IO) {
        // 1. Save user message to database
        val userMsg = ChatMessage(sender = "user", text = userText)
        chatMessageDao.insertMessage(userMsg)

        // 2. Fetch past conversation for context
        val apiKey = BuildConfig.GEMINI_API_KEY
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = userText)))),
            systemInstruction = SystemInstruction(parts = listOf(Part(text = systemPrompt)))
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Sorry, connection issues ki wajah se main answer nahi kar paa rahi hoon. Please try again!"
            
            val tutorMsg = ChatMessage(sender = "tutor", text = replyText)
            chatMessageDao.insertMessage(tutorMsg)
        } catch (e: Exception) {
            val errorMsg = ChatMessage(sender = "tutor", text = "Oops! Kuch network error ho gaya hai. Aapka internet check karke dobara puchiye! (Error: ${e.localizedMessage})")
            chatMessageDao.insertMessage(errorMsg)
        }
    }

    suspend fun generateQuizQuestion(subject: String): QuizSession? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        val prompt = """
            Generate a single high-quality multiple choice question (MCQ) for JEE Mains/Advanced level in the subject: $subject.
            The question text, options, and explanation MUST be in natural Hinglish.
            You MUST format your reply strictly as follows so that it can be parsed:
            
            QUESTION: [Your Hinglish question text here. Add proper formulas if required.]
            A) [Option A text]
            B) [Option B text]
            C) [Option C text]
            D) [Option D text]
            CORRECT: [Must be exactly A, B, C, or D]
            EXPLANATION: [Detailed step-by-step Hinglish explanation of how to solve, using equations.]
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = SystemInstruction(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return@withContext null
            
            val parsedSession = parseQuizSession(rawText, subject)
            if (parsedSession != null) {
                val id = quizSessionDao.insertSession(parsedSession)
                return@withContext parsedSession.copy(id = id.toInt())
            }
            return@withContext null
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    suspend fun submitQuizAnswer(session: QuizSession, selectedOption: String) = withContext(Dispatchers.IO) {
        val updated = session.copy(userSelectedAnswer = selectedOption)
        quizSessionDao.updateSession(updated)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        chatMessageDao.clearAllMessages()
        quizSessionDao.clearAllSessions()
    }

    private fun parseQuizSession(rawText: String, subject: String): QuizSession? {
        try {
            val questionRegex = "QUESTION:\\s*(.*?)\\s*(?=A\\)|B\\)|C\\)|D\\)|CORRECT:|EXPLANATION:|\$)".toRegex(RegexOption.DOT_MATCHES_ALL)
            val optARegex = "A\\)\\s*(.*?)\\s*(?=B\\)|C\\)|D\\)|CORRECT:|EXPLANATION:|\$)".toRegex(RegexOption.DOT_MATCHES_ALL)
            val optBRegex = "B\\)\\s*(.*?)\\s*(?=C\\)|D\\)|CORRECT:|EXPLANATION:|\$)".toRegex(RegexOption.DOT_MATCHES_ALL)
            val optCRegex = "C\\)\\s*(.*?)\\s*(?=D\\)|CORRECT:|EXPLANATION:|\$)".toRegex(RegexOption.DOT_MATCHES_ALL)
            val optDRegex = "D\\)\\s*(.*?)\\s*(?=CORRECT:|EXPLANATION:|\$)".toRegex(RegexOption.DOT_MATCHES_ALL)
            val correctRegex = "CORRECT:\\s*([A-D])".toRegex()
            val explanationRegex = "EXPLANATION:\\s*(.*)".toRegex(RegexOption.DOT_MATCHES_ALL)

            val question = questionRegex.find(rawText)?.groupValues?.get(1)?.trim() ?: ""
            val optA = optARegex.find(rawText)?.groupValues?.get(1)?.trim() ?: ""
            val optB = optBRegex.find(rawText)?.groupValues?.get(1)?.trim() ?: ""
            val optC = optCRegex.find(rawText)?.groupValues?.get(1)?.trim() ?: ""
            val optD = optDRegex.find(rawText)?.groupValues?.get(1)?.trim() ?: ""
            val correct = correctRegex.find(rawText)?.groupValues?.get(1)?.trim() ?: "A"
            val explanation = explanationRegex.find(rawText)?.groupValues?.get(1)?.trim() ?: ""

            if (question.isNotEmpty() && optA.isNotEmpty() && optB.isNotEmpty()) {
                return QuizSession(
                    subject = subject,
                    questionText = question,
                    optionA = optA,
                    optionB = optB,
                    optionC = optC,
                    optionD = optD,
                    correctAnswer = correct,
                    explanation = explanation
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
