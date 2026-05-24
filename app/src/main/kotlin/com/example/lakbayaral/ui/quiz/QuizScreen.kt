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
import com.example.lakbayaral.data.models.Municipality
import com.example.lakbayaral.data.models.QuizQuestion
import com.example.lakbayaral.data.models.QuizResult
import com.example.lakbayaral.data.repository.FirebaseRepository
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
        val total       : Int,
        val question    : QuizQuestion,
        val selected    : Int? = null,
        val showFeedback: Boolean = false
    ) : QuizPhase()
    data class Result(val result: QuizResult) : QuizPhase()
}

// ── ViewModel ─────────────────────────────────────────────────────────────────
@HiltViewModel
class QuizViewModel @Inject constructor(
    private val progressRepo: UserProgressRepository,
    private val firebaseRepo: FirebaseRepository
) : ViewModel() {

    private val _phase = MutableStateFlow<QuizPhase>(QuizPhase.Intro)
    val phase: StateFlow<QuizPhase> = _phase

    private val _unlockChoices = MutableStateFlow<List<Municipality>>(emptyList())
    val unlockChoices: StateFlow<List<Municipality>> = _unlockChoices

    private var questions: List<QuizQuestion> = emptyList()
    private var municipalityId: String = ""
    private var score = 0

    fun load(id: String) {
        municipalityId = id
        _phase.value = QuizPhase.Intro
        _unlockChoices.value = emptyList()
        val municipality = LocalDataSource.getMunicipality(id)
        questions = if (firebaseRepo.isSignedIn()) {
            municipality?.onlineQuiz?.takeIf { it.isNotEmpty() } ?: municipality?.quiz ?: emptyList()
        } else {
            municipality?.quiz ?: emptyList()
        }
    }

    fun startQuiz() {
        score = 0
        if (questions.isEmpty()) return
        _phase.value = QuizPhase.Question(0, questions.size, questions[0])
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
            _phase.value = QuizPhase.Question(nextIndex, questions.size, questions[nextIndex])
        } else {
            val passed = score.toFloat() / questions.size >= 0.70f
            val result = QuizResult(
                municipalityId = municipalityId,
                score          = score,
                total          = questions.size,
                passed         = passed
            )
            progressRepo.saveQuizResult(result)
            _unlockChoices.value = if (passed) {
                progressRepo.getMunicipalityUnlockChoices(municipalityId)
            } else {
                emptyList()
            }
            _phase.value = QuizPhase.Result(result)
        }
    }

    fun unlockMunicipalityChoice(id: String): Boolean {
        val canUnlock = _unlockChoices.value.any { it.id == id }
        if (!canUnlock) return false

        progressRepo.unlockMunicipality(id)
        _unlockChoices.value = emptyList()
        return true
    }

    fun retry() {
        score        = 0
        _unlockChoices.value = emptyList()
        _phase.value = QuizPhase.Question(0, questions.size, questions[0])
    }
}

// ── Screen ─────────────────────────────────────────────────────────────────────
@Composable
fun QuizScreen(
    municipalityId: String,
    onFinished    : (String?) -> Unit,
    onBack        : () -> Unit,
    viewModel     : QuizViewModel = hiltViewModel()
) {
    val phase by viewModel.phase.collectAsState()
    val unlockChoices by viewModel.unlockChoices.collectAsState()

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
                is QuizPhase.Result   -> QuizResultView(
                    result = currentPhase.result,
                    unlockChoices = unlockChoices,
                    onRetry = viewModel::retry,
                    onUnlockChoice = { id ->
                        if (viewModel.unlockMunicipalityChoice(id)) onFinished(id)
                    },
                    onFinish = { onFinished(null) }
                )
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
            QuizRule("🔓", "Pass to choose the next municipality to unlock")
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
            progress = { (state.index + 1).toFloat() / state.total.coerceAtLeast(1) },
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
fun QuizResultView(
    result: QuizResult,
    unlockChoices: List<Municipality>,
    onRetry: () -> Unit,
    onUnlockChoice: (String) -> Unit,
    onFinish: () -> Unit
) {
    val emoji    = if (result.passed) "🏆" else "😔"
    val title    = if (result.passed) "QUIZ PASSED!" else "QUIZ FAILED"
    val color    = if (result.passed) SuccessGreen else ErrorRed

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
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
            if (unlockChoices.isNotEmpty()) {
                Text(
                    "Choose the next municipality to unlock",
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                unlockChoices.forEach { municipality ->
                    MunicipalityUnlockChoiceCard(
                        municipality = municipality,
                        onClick = { onUnlockChoice(municipality.id) }
                    )
                    Spacer(Modifier.height(10.dp))
                }
            } else {
                Text(
                    "All available municipalities are unlocked.",
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Text("You need 70% to pass. Keep studying!", color = WhiteGlow.copy(0.6f), textAlign = TextAlign.Center)
        }

        Spacer(Modifier.height(if (result.passed && unlockChoices.isNotEmpty()) 12.dp else 32.dp))

        if (!result.passed) {
            NeonButton(text = "RETRY QUIZ", onClick = onRetry, modifier = Modifier.fillMaxWidth().height(52.dp), glowColor = NeonBlue)
            Spacer(Modifier.height(12.dp))
        }

        if (!result.passed || unlockChoices.isEmpty()) {
            NeonButton(
                text     = "BACK TO MAP",
                onClick  = onFinish,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                glowColor = if (result.passed) Gold else WhiteGlow
            )
        } else {
            TextButton(onClick = onFinish) {
                Text("Back to Map", color = WhiteGlow.copy(0.56f))
            }
        }
    }
}

@Composable
fun MunicipalityUnlockChoiceCard(municipality: Municipality, onClick: () -> Unit) {
    val preview = if (municipality.description.length > 104) {
        municipality.description.take(104) + "..."
    } else {
        municipality.description
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.horizontalGradient(listOf(Gold.copy(0.14f), NeonBlue.copy(0.1f))))
            .border(1.dp, Gold.copy(0.45f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(municipality.name, color = Gold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(municipality.province, color = NeonBlue, fontSize = 11.sp)
                }
                Text("UNLOCK", color = SuccessGreen, fontWeight = FontWeight.Black, fontSize = 11.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(preview, color = WhiteGlow.copy(0.66f), fontSize = 12.sp, lineHeight = 17.sp)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                UnlockChoiceStat("${municipality.historicalPlaces.size}", "Places")
                UnlockChoiceStat("${municipality.historicalPeople.size}", "People")
                UnlockChoiceStat("${municipality.quiz.size}", "Quiz")
            }
        }
    }
}

@Composable
fun UnlockChoiceStat(value: String, label: String) {
    Row(
        modifier = Modifier
            .background(Color.White.copy(0.06f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(value, color = Gold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(Modifier.width(4.dp))
        Text(label, color = WhiteGlow.copy(0.52f), fontSize = 10.sp)
    }
}

@Composable
fun ResultStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 22.sp)
        Text(label, color = WhiteGlow.copy(0.5f), fontSize = 11.sp)
    }
}
