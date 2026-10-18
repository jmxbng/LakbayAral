package com.example.lakbayaral.ui.municipality

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.example.lakbayaral.data.models.Municipality
import com.example.lakbayaral.data.repository.LocalDataSource
import com.example.lakbayaral.data.repository.UserProgressRepository
import com.example.lakbayaral.ui.components.*
import com.example.lakbayaral.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel as HVM
import javax.inject.Inject

@HiltViewModel
class MunicipalityViewModel @Inject constructor(
    private val progressRepo: UserProgressRepository
) : ViewModel() {
    fun getMunicipality(id: String): Municipality? = LocalDataSource.getMunicipality(id)
    fun isMunicipalityUnlocked(id: String): Boolean = progressRepo.isMunicipalityUnlocked(id)
    fun hasCompletedQuiz(id: String): Boolean = progressRepo.progress.quizResults[id]?.passed == true
}

@Composable
fun MunicipalityScreen(
    municipalityId: String,
    onARPlaces    : () -> Unit,
    onARPeople    : () -> Unit,
    onQuiz        : () -> Unit,
    onBack        : () -> Unit,
    viewModel     : MunicipalityViewModel = hiltViewModel()
) {
    val municipality  = viewModel.getMunicipality(municipalityId)
    val isUnlocked = viewModel.isMunicipalityUnlocked(municipalityId)
    val quizCompleted = viewModel.hasCompletedQuiz(municipalityId)

    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue  = if (visible) 1f else 0f,
        animationSpec = tween(600),
        label = "entrance"
    )
    LaunchedEffect(Unit) { visible = true }

    if (municipality == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Municipality not found", color = ErrorRed)
        }
        return
    }

    if (!isUnlocked) {
        DarkBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🔒", fontSize = 64.sp)
                Spacer(Modifier.height(16.dp))
                GoldTitle("DESTINATION LOCKED")
                Spacer(Modifier.height(12.dp))
                Text(
                    "${municipality.name} is not unlocked yet. Complete the required quiz sequence to explore it.",
                    color = WhiteGlow.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(28.dp))
                NeonButton("BACK TO MAP", onClick = onBack, modifier = Modifier.fillMaxWidth().height(52.dp))
            }
        }
        return
    }

    DarkBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha)
        ) {
            GameTopBar(title = municipality.name.uppercase(), onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Hero banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    NeonBlue.copy(alpha = 0.15f),
                                    Gold.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏛️", fontSize = 52.sp)
                        Text(
                            "${municipality.name}, ${municipality.province}",
                            color = Gold, fontWeight = FontWeight.Bold, fontSize = 16.sp
                        )
                    }
                }

                // Description card
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text("ABOUT ${municipality.name.uppercase()}", color = NeonBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(municipality.description, color = WhiteGlow.copy(alpha = 0.85f), fontSize = 14.sp, lineHeight = 22.sp)
                }

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatChip("🏛️", "${municipality.historicalPlaces.size}", "Places", modifier = Modifier.weight(1f))
                    StatChip("👤", "${municipality.historicalPeople.size}", "People", modifier = Modifier.weight(1f))
                    StatChip("❓", "${municipality.quiz.size}", "Questions", modifier = Modifier.weight(1f))
                }

                // AR Mode selection
                Text(
                    "SELECT AR MODE",
                    color = Gold, fontWeight = FontWeight.Bold, fontSize = 12.sp,
                    letterSpacing = 2.sp, modifier = Modifier.fillMaxWidth()
                )

                ARModeCard(
                    emoji    = "🏛️",
                    title    = "AR Historical Places",
                    subtitle = "Explore churches, bridges & landmarks in AR",
                    onClick  = onARPlaces
                )

                ARModeCard(
                    emoji    = "👤",
                    title    = "AR Historical People",
                    subtitle = "Meet local heroes as 3D holograms in AR",
                    onClick  = onARPeople,
                    color    = NeonBlue
                )

                // Quiz button
                Divider(color = WhiteGlow.copy(alpha = 0.1f))

                NeonButton(
                    text      = if (quizCompleted) "✓ QUIZ COMPLETED" else "TAKE THE QUIZ",
                    onClick   = onQuiz,
                    modifier  = Modifier.fillMaxWidth().height(52.dp),
                    glowColor = if (quizCompleted) SuccessGreen else Gold
                )

                if (!quizCompleted) {
                    Text(
                        "Pass the quiz (≥70%) to automatically unlock the next municipality",
                        color = WhiteGlow.copy(0.4f), fontSize = 11.sp, textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ARModeCard(emoji: String, title: String, subtitle: String, onClick: () -> Unit, color: Color = Gold) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border   = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 32.sp)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title,    color = color, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(subtitle, color = WhiteGlow.copy(0.5f), fontSize = 12.sp)
            }
            Text("›", color = color, fontSize = 22.sp)
        }
    }
}

@Composable
fun StatChip(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color.White.copy(0.05f), RoundedCornerShape(10.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 18.sp)
        Text(value, color = Gold, fontWeight = FontWeight.Black, fontSize = 18.sp)
        Text(label, color = WhiteGlow.copy(0.5f), fontSize = 11.sp)
    }
}
