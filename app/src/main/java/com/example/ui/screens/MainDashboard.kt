package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.ChatMessage
import com.example.data.model.FormulaDataProvider
import com.example.data.model.QuizSession
import com.example.ui.viewmodel.TutorViewModel
import kotlinx.coroutines.launch

// Custom Color Palette for High-Contrast Modern Theme
val DarkBackground = Color(0xFF0C0E14)
val SurfaceCardColor = Color(0xFF161923)
val SurfaceCardElevated = Color(0xFF1F2332)
val TextPrimary = Color(0xFFF1F3F9)
val TextSecondary = Color(0xFF9EA6B8)
val AccentCyan = Color(0xFF00D4FF)
val AccentOrange = Color(0xFFFF7E40)
val IncorrectRed = Color(0xFFFF5252)
val CorrectGreen = Color(0xFF00E676)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboard(
    viewModel: TutorViewModel,
    modifier: Modifier = Modifier
) {
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val activeTab = viewModel.activeTab
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Tutor Portrait Avatar
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .border(2.dp, AccentCyan, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_tutor_avatar_1783316798834),
                                contentDescription = "Ananya AI Tutor Portrait",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Ananya • JEE AI Guru",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(CorrectGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Active Now • Hinglish Mentor",
                                    color = AccentCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.resetHistory() },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = IncorrectRed)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Chat and quiz sessions history"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceCardColor
                )
            )
        },
        bottomBar = {
            // Modern Floating bottom tab bar
            NavigationBar(
                containerColor = SurfaceCardColor,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == "chat",
                    onClick = { viewModel.activeTab = "chat" },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Doubt Solver Chat Tab") },
                    label = { Text("Doubt Solver", fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentCyan,
                        selectedTextColor = AccentCyan,
                        indicatorColor = SurfaceCardElevated,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "quiz",
                    onClick = { viewModel.activeTab = "quiz" },
                    icon = { Icon(Icons.Default.Quiz, contentDescription = "JEE Practice Quiz Tab") },
                    label = { Text("Practice Quiz", fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentOrange,
                        selectedTextColor = AccentOrange,
                        indicatorColor = SurfaceCardElevated,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "formulas",
                    onClick = { viewModel.activeTab = "formulas" },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Formula Cheat-Sheet Tab") },
                    label = { Text("Formulas", fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentCyan,
                        selectedTextColor = AccentCyan,
                        indicatorColor = SurfaceCardElevated,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            when (activeTab) {
                "chat" -> ChatScreen(
                    messages = chatMessages,
                    isLoading = viewModel.isChatLoading,
                    onSendMessage = { viewModel.sendChatMessage(it) }
                )
                "quiz" -> QuizScreen(
                    viewModel = viewModel
                )
                "formulas" -> FormulasScreen()
            }
        }
    }
}

@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Automatically scroll to bottom when new messages arrive
    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Quick suggestions row
        val suggestions = listOf(
            "Projectile Motion formulae?",
            "Organic resonance kya hai?",
            "Solve: x² + 5x + 6 = 0 roots",
            "Gibbs energy spontaneity explain"
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1.getVerticalWeight())
                .fillMaxWidth()
        ) {
            items(messages) { message ->
                ChatBubble(message = message)
            }

            if (isLoading) {
                item {
                    TutorTypingIndicator()
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick suggestions container
        if (messages.size <= 1 && !isLoading) {
            Text(
                text = "Puchiye apna doubt, jaise:",
                color = AccentCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                suggestions.take(2).forEach { suggestion ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceCardColor)
                            .clickable { onSendMessage(suggestion) }
                            .padding(8.dp)
                            .border(1.dp, SurfaceCardElevated, RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = suggestion,
                            color = TextPrimary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Input Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceCardColor)
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask doubt in Hinglish...", color = TextSecondary, fontSize = 14.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                        keyboardController?.hide()
                    }
                }),
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                        keyboardController?.hide()
                    }
                },
                enabled = inputText.isNotBlank(),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = AccentCyan,
                    disabledContentColor = TextSecondary
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send doubt to Ananya tutor"
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == "user"
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bgBrush = if (isUser) {
        Brush.horizontalGradient(listOf(Color(0xFF005C8A), Color(0xFF008AA8)))
    } else {
        Brush.horizontalGradient(listOf(SurfaceCardColor, SurfaceCardColor))
    }
    val roundedCorners = if (isUser) {
        RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp)
    }

    Column(
        horizontalAlignment = alignment,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 310.dp)
                .clip(roundedCorners)
                .background(bgBrush)
                .border(
                    width = 1.dp,
                    color = if (isUser) AccentCyan.copy(alpha = 0.3f) else SurfaceCardElevated,
                    shape = roundedCorners
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = if (isUser) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@Composable
fun TutorTypingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCardColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_tutor_avatar_1783316798834),
                contentDescription = "Ananya mini portrait",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Ananya is thinking...",
            color = AccentCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(8.dp))
        CircularProgressIndicator(
            modifier = Modifier.size(12.dp),
            color = AccentCyan,
            strokeWidth = 2.dp
        )
    }
}

@Composable
fun QuizScreen(viewModel: TutorViewModel) {
    var selectedSubject by remember { mutableStateOf("Physics") }
    val currentQuiz = viewModel.currentQuiz

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "JEE Practice Arena",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Ananya aapke liye JEE standard question aur Hinglish explanation select karengi.",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Subject selector chip row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("Physics", "Chemistry", "Mathematics").forEach { subject ->
                val isSelected = selectedSubject == subject
                val bg = if (isSelected) AccentOrange else SurfaceCardColor
                val textColor = if (isSelected) DarkBackground else TextPrimary

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bg)
                        .clickable { selectedSubject = subject }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = subject,
                        color = textColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (currentQuiz == null) {
            // No active quiz screen
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1.getVerticalWeight())
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Quiz,
                    contentDescription = "Quiz default symbol",
                    tint = AccentOrange,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "$selectedSubject test start kijiye!",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ananya generate karengi conceptual high-quality JEE questions.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (viewModel.isQuizLoading) {
                    CircularProgressIndicator(color = AccentOrange)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ananya is formulating a JEE question...",
                        color = AccentOrange,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Button(
                        onClick = { viewModel.startNewQuiz(selectedSubject) },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Start JEE Quiz", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                if (viewModel.quizStatusMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = viewModel.quizStatusMessage,
                        color = IncorrectRed,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Active quiz screen
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                item {
                    // Question box card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceCardColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "JEE ${currentQuiz.subject} MCQ",
                                    color = AccentOrange,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Correct: +4 | Wrong: -1",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentQuiz.questionText,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Options
                val options = listOf(
                    "A" to currentQuiz.optionA,
                    "B" to currentQuiz.optionB,
                    "C" to currentQuiz.optionC,
                    "D" to currentQuiz.optionD
                )

                items(options) { (label, text) ->
                    val isAnswered = currentQuiz.userSelectedAnswer != null
                    val isSelected = currentQuiz.userSelectedAnswer == label
                    val isCorrect = currentQuiz.correctAnswer == label

                    val optionColor = when {
                        !isAnswered -> SurfaceCardColor
                        isCorrect -> CorrectGreen.copy(alpha = 0.2f)
                        isSelected && !isCorrect -> IncorrectRed.copy(alpha = 0.2f)
                        else -> SurfaceCardColor
                    }

                    val borderColor = when {
                        !isAnswered -> if (isSelected) AccentOrange else SurfaceCardElevated
                        isCorrect -> CorrectGreen
                        isSelected && !isCorrect -> IncorrectRed
                        else -> SurfaceCardElevated
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(optionColor)
                            .clickable(enabled = !isAnswered) {
                                viewModel.submitQuizOption(label)
                            }
                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) AccentOrange else SurfaceCardElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) DarkBackground else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = text,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Feedback / Explanation Panel
                if (currentQuiz.userSelectedAnswer != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceCardElevated),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.img_tutor_avatar_1783316798834),
                                            contentDescription = "Ananya explanation guide avatar",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Guru Ananya's Explanation:",
                                        color = AccentCyan,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = currentQuiz.explanation,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        // Try another question button
                        Button(
                            onClick = { viewModel.startNewQuiz(selectedSubject) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(bottom = 16.dp)
                        ) {
                            Text("Next JEE MCQ", color = DarkBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormulasScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val formulaCategories = FormulaDataProvider.categories

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "JEE Formula Sheets",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Most important formulas handpicked by Guru Ananya with tip cards.",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search categories (e.g., Electrodynamics)...", color = TextSecondary) },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon", tint = TextSecondary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = AccentCyan,
                unfocusedBorderColor = SurfaceCardElevated,
                focusedContainerColor = SurfaceCardColor,
                unfocusedContainerColor = SurfaceCardColor
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        val filteredCategories = formulaCategories.filter {
            it.categoryName.contains(searchQuery, ignoreCase = true) ||
            it.subject.contains(searchQuery, ignoreCase = true)
        }

        if (filteredCategories.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("Aisa koi topic nahi mila. Try searching Physics or Math!", color = TextSecondary, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredCategories) { category ->
                    Text(
                        text = "${category.subject} • ${category.categoryName}",
                        color = if (category.subject == "Physics") AccentCyan else AccentOrange,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    category.formulas.forEach { formula ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceCardColor),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = formula.title,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                // Mathematical Expression Display Box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SurfaceCardElevated)
                                        .border(1.dp, AccentCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = formula.expression,
                                        color = AccentCyan,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 14.sp,
                                        lineHeight = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = formula.description,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                // Guru Tips
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AccentOrange.copy(alpha = 0.1f))
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Tip sign",
                                        tint = AccentOrange,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Tip: ${formula.note}",
                                        color = AccentOrange,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Utility function to support Float layout weights without ambiguous JVM signatures
private fun Int.getVerticalWeight(): Float = this.toFloat()
