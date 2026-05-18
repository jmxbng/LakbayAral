package com.example.lakbayaral.ui.quiz

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.example.lakbayaral.data.models.QuizQuestion
import com.example.lakbayaral.data.models.QuizResult
import com.example.lakbayaral.data.repository.LocalDataSource
import com.example.lakbayaral.data.repository.UserProgressRepository
import com.example.lakbayaral.ui.components.*
import com.example.lakbayaral.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

// ── Quiz State ─────────────────────────────────────────────────────────────────
sealed class QuizPhase {
    object Intro    : QuizPhase()
    data class Question(
        val index       : Int,
        val question    : QuizQuestion,
        val selected    : Int? = null,
        val showFeedback: Boolean = false
    ) : QuizPhase()
    data class Result(val result: QuizResult) : QuizPhase()
}

// ── ViewModel ─────────────────────────────────────────────────────────────────
@HiltViewModel
class QuizViewModel @Inject constructor(
    private val progressRepo: UserProgressRepository
) : ViewModel() {

    private val _phase = MutableStateFlow<QuizPhase>(QuizPhase.Intro)
    val phase: StateFlow<QuizPhase> = _phase

    private var questions: List<QuizQuestion> = emptyList()
    private var municipalityId: String = ""
    private var score = 0

    fun load(id: String) {
        municipalityId = id
        questions      = LocalDataSource.getMunicipality(id)?.quiz ?: emptyList()
    }

    fun startQuiz() {
        score = 0
        if (questions.isEmpty()) return
        _phase.value = QuizPhase.Question(0, questions[0])
    }

    fun selectAnswer(choiceIndex: Int) {
        val current = _phase.value as? QuizPhase.Question ?: return
        if (current.selected != null) return // already answered

        _phase.value = current.copy(selected = choiceIndex, showFeedback = true)

        if (choiceIndex == current.question.correctIndex) score++
    }

    fun nextQuestion() {
        val current = _phase.value as? QuizPhase.Question ?: return
        if (current.selected == null) return
        advance(current.index)
    }

    private fun advance(currentIndex: Int) {
        val nextIndex = currentIndex + 1
        if (nextIndex < questions.size) {
            _phase.value = QuizPhase.Question(nextIndex, questions[nextIndex])
        } else {
            val passed = score.toFloat() / questions.size >= 0.70f
            val result = QuizResult(
                municipalityId = municipalityId,
                score          = score,
                total          = questions.size,
                passed         = passed
            )
            progressRepo.saveQuizResult(result)
            _phase.value = QuizPhase.Result(result)
        }
    }

    fun retry() {
        score        = 0
        _phase.value = QuizPhase.Question(0, questions[0])
    }
}

// ── Screen ─────────────────────────────────────────────────────────────────────
@Composable
fun QuizScreen(
    municipalityId: String,
    onFinished    : () -> Unit,
    onBack        : () -> Unit,
    viewModel     : QuizViewModel = hiltViewModel()
) {
    val phase by viewModel.phase.collectAsState()

    LaunchedEffect(municipalityId) { viewModel.load(municipalityId) }

    DarkBackground {
        AnimatedContent(
            targetState   = phase,
            transitionSpec = {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith
                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            },
            label = "quiz_phase"
        ) { currentPhase ->
            when (currentPhase) {
                is QuizPhase.Intro    -> QuizIntroView(municipalityId, onBack, onStart = viewModel::startQuiz)
                is QuizPhase.Question -> QuizQuestionView(
                    state = currentPhase,
                    onSelect = viewModel::selectAnswer,
                    onNext = viewModel::nextQuestion,
                    onExit = onBack
                )
                is QuizPhase.Result   -> QuizResultView(currentPhase.result, onRetry = viewModel::retry, onFinish = onFinished)
            }
        }
    }
}

// ── Intro ──────────────────────────────────────────────────────────────────────
@Composable
fun QuizIntroView(municipalityId: String, onBack: () -> Unit, onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📝", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        GoldTitle("HISTORY QUIZ")
        Spacer(Modifier.height(8.dp))
        Text(
            LocalDataSource.getMunicipality(municipalityId)?.name ?: "",
            color = NeonBlue, fontSize = 16.sp, fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(24.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            QuizRule("✅", "Answer multiple-choice questions")
            QuizRule("🎯", "Passing score: 70% or higher")
            QuizRule("🔓", "Pass to unlock the next municipality")
            QuizRule("🔄", "You can retry if you fail")
        }

        Spacer(Modifier.height(32.dp))

        NeonButton(text = "START QUIZ", onClick = onStart, modifier = Modifier.fillMaxWidth().height(52.dp))
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onBack) {
            Text("← Go Back", color = WhiteGlow.copy(0.5f))
        }
    }
}

@Composable
fun QuizRule(emoji: String, text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = 16.sp)
        Spacer(Modifier.width(10.dp))
        Text(text, color = WhiteGlow.copy(0.8f), fontSize = 14.sp)
    }
}

// ── Question ──────────────────────────────────────────────────────────────────
@Composable
fun QuizQuestionView(
    state: QuizPhase.Question,
    onSelect: (Int) -> Unit,
    onNext: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Question ${state.index + 1}", color = WhiteGlow.copy(0.4f), fontSize = 12.sp)
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onExit) {
                Text("Exit Quiz", color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Progress bar
        LinearProgressIndicator(
            progress = { (state.index + 1).toFloat() / 8f },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
            color    = Gold,
            trackColor = WhiteGlow.copy(0.1f)
        )

        Spacer(Modifier.height(24.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(state.question.question, color = WhiteGlow, fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(20.dp))

        state.question.choices.forEachIndexed { index, choice ->
            val isSelected = state.selected == index
            val isCorrect  = index == state.question.correctIndex
            val isWrong    = isSelected && !isCorrect

            val borderColor = when {
                state.showFeedback && isCorrect -> SuccessGreen
                state.showFeedback && isWrong   -> ErrorRed
                isSelected                      -> Gold
                else                            -> WhiteGlow.copy(0.15f)
            }
            val bgColor = when {
                state.showFeedback && isCorrect -> SuccessGreen.copy(0.1f)
                state.showFeedback && isWrong   -> ErrorRed.copy(0.1f)
                isSelected                      -> Gold.copy(0.08f)
                else                            -> Color.White.copy(0.04f)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                    .clickable(enabled = state.selected == null) { onSelect(index) }
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(50))
                            .background(borderColor.copy(0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when {
                                state.showFeedback && isCorrect -> "✓"
                                state.showFeedback && isWrong   -> "✗"
                                else -> ('A' + index).toString()
                            },
                            color = borderColor, fontWeight = FontWeight.Bold, fontSize = 14.sp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(choice, color = WhiteGlow.copy(0.9f), fontSize = 14.sp)
                }
            }
        }

        // Explanation after answer
        AnimatedVisibility(visible = state.showFeedback && state.question.explanation.isNotEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), borderColor = NeonBlue.copy(0.5f)) {
                Text("💡 ${state.question.explanation}", color = NeonBlue, fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(18.dp))

        NeonButton(
            text = "NEXT",
            onClick = onNext,
            enabled = state.selected != null,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            glowColor = if (state.selected != null) Gold else WhiteGlow.copy(alpha = 0.35f)
        )

        Spacer(Modifier.height(32.dp))
    }
}

// ── Result ────────────────────────────────────────────────────────────────────
@Composable
fun QuizResultView(result: QuizResult, onRetry: () -> Unit, onFinish: () -> Unit) {
    val emoji    = if (result.passed) "🏆" else "😔"
    val title    = if (result.passed) "QUIZ PASSED!" else "QUIZ FAILED"
    val color    = if (result.passed) SuccessGreen else ErrorRed

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 72.sp)
        Spacer(Modifier.height(16.dp))
        Text(title, color = color, fontWeight = FontWeight.Black, fontSize = 26.sp, letterSpacing = 2.sp)

        Spacer(Modifier.height(24.dp))

        GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = color.copy(0.5f)) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                ResultStat("Score", "${result.score}/${result.total}", color)
                ResultStat("Accuracy", "${result.percentage}%", color)
                ResultStat("Status", if (result.passed) "PASS" else "FAIL", color)
            }
        }

        Spacer(Modifier.height(16.dp))

        if (result.passed) {
            Text("🔓 Next municipality unlocked!", color = SuccessGreen, fontWeight = FontWeight.Bold)
        } else {
            Text("You need 70% to pass. Keep studying!", color = WhiteGlow.copy(0.6f), textAlign = TextAlign.Center)
        }

        Spacer(Modifier.height(32.dp))

        if (!result.passed) {
            NeonButton(text = "RETRY QUIZ", onClick = onRetry, modifier = Modifier.fillMaxWidth().height(52.dp), glowColor = NeonBlue)
            Spacer(Modifier.height(12.dp))
        }

        NeonButton(
            text     = if (result.passed) "CONTINUE →" else "BACK TO MAP",
            onClick  = onFinish,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            glowColor = if (result.passed) Gold else WhiteGlow
        )
    }
}

@Composable
fun ResultStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 22.sp)
        Text(label, color = WhiteGlow.copy(0.5f), fontSize = 11.sp)
    }
}
