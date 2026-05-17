package com.example.lakbayaral.ui.mainmenu

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.example.lakbayaral.data.repository.UserProgressRepository
import com.example.lakbayaral.ui.components.*
import com.example.lakbayaral.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val progressRepo: UserProgressRepository
) : ViewModel() {
    val playerName   get() = progressRepo.getPlayerName()
    val totalScore   get() = progressRepo.getTotalScore()
    val unlockedCount get() = progressRepo.progress.unlockedMunicipalities.size
}

@Composable
fun MainMenuScreen(
    onStartExploration: () -> Unit,
    onLeaderboard     : () -> Unit,
    viewModel         : MainMenuViewModel = hiltViewModel()
) {
    val infiniteTransition = rememberInfiniteTransition(label = "menu")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "glow"
    )

    DarkBackground {
        // Top decorative gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(NeonBlue.copy(alpha = 0.06f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // Logo area
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .scale(glowScale)
                    .background(
                        Brush.radialGradient(listOf(Gold.copy(alpha = 0.3f), Color.Transparent))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🏛️", fontSize = 50.sp)
            }

            Spacer(Modifier.height(12.dp))

            Text("LAKBAY ARAL", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Gold, letterSpacing = 3.sp)
            Text("Pampanga Heritage Explorer", fontSize = 12.sp, color = NeonBlue, letterSpacing = 1.sp)

            Spacer(Modifier.height(24.dp))

            // Player stats card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment   = Alignment.CenterVertically
                ) {
                    Column {
                        Text("EXPLORER", color = NeonBlue, fontSize = 10.sp, letterSpacing = 1.sp)
                        Text(viewModel.playerName, color = WhiteGlow, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("TOTAL SCORE", color = NeonBlue, fontSize = 10.sp, letterSpacing = 1.sp)
                        Text("${viewModel.totalScore}", color = Gold, fontWeight = FontWeight.Black, fontSize = 22.sp)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PulsingDot(color = SuccessGreen, size = 8.dp)
                    Spacer(Modifier.width(6.dp))
                    Text("${viewModel.unlockedCount} municipality unlocked", color = SuccessGreen, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(32.dp))

            // Main navigation buttons
            MenuButton(
                icon     = Icons.Default.Explore,
                label    = "START EXPLORATION",
                subtitle = "Enter AR Historical Mode",
                onClick  = onStartExploration,
                primary  = true
            )
            Spacer(Modifier.height(12.dp))
            MenuButton(
                icon     = Icons.Default.EmojiEvents,
                label    = "LEADERBOARD",
                subtitle = "Global Rankings",
                onClick  = onLeaderboard
            )

            Spacer(Modifier.weight(1f))

            Text(
                text      = "v1.0 · Pampanga Heritage Series",
                fontSize  = 10.sp,
                color     = WhiteGlow.copy(alpha = 0.2f),
                modifier  = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun MenuButton(
    icon    : ImageVector,
    label   : String,
    subtitle: String,
    onClick : () -> Unit,
    primary : Boolean = false
) {
    val bgColor  = if (primary) Gold.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.05f)
    val fgColor  = if (primary) Gold else WhiteGlow
    val borderC  = if (primary) Gold.copy(alpha = 0.6f) else WhiteGlow.copy(alpha = 0.15f)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = bgColor),
        border   = androidx.compose.foundation.BorderStroke(1.dp, borderC)
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label, tint = fgColor, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(label, color = fgColor, fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = 1.sp)
                Text(subtitle, color = fgColor.copy(alpha = 0.6f), fontSize = 12.sp)
            }
            Spacer(Modifier.weight(1f))
            Text("›", color = fgColor, fontSize = 22.sp)
        }
    }
}
