package com.example.lakbayaral.ui.leaderboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lakbayaral.data.models.Player
import com.example.lakbayaral.data.repository.UserProgressRepository
import com.example.lakbayaral.ui.components.*
import com.example.lakbayaral.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val progressRepo : UserProgressRepository
) : ViewModel() {

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { loadLeaderboard() }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            _players.value = progressRepo.getLeaderboardPlayers()
            _isLoading.value = false
        }
    }

    fun uploadMyScore() {
        viewModelScope.launch {
            progressRepo.saveLocalLeaderboardEntry()
            loadLeaderboard()
        }
    }
}

@Composable
fun LeaderboardScreen(
    onBack    : () -> Unit,
    viewModel : LeaderboardViewModel = hiltViewModel()
) {
    val players   by viewModel.players.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    DarkBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            GameTopBar(title = "LEADERBOARD", onBack = onBack)

            // Upload button
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = viewModel::uploadMyScore) {
                    Text("↑ Submit My Score", color = NeonBlue, fontSize = 12.sp)
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Gold)
                }
            } else if (players.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏆", fontSize = 48.sp)
                        Text("No rankings yet", color = WhiteGlow.copy(0.5f))
                        Text("Complete a quiz to appear here!", color = WhiteGlow.copy(0.3f), fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        // Top 3 podium
                        if (players.size >= 3) {
                            PodiumRow(top3 = players.take(3))
                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    itemsIndexed(players.drop(if (players.size >= 3) 3 else 0)) { index, player ->
                        val rank = index + 4
                        LeaderboardRow(rank = rank, player = player)
                    }
                }
            }
        }
    }
}

@Composable
fun PodiumRow(top3: List<Player>) {
    val order = if (top3.size >= 2) listOf(1, 0, 2) else listOf(0) // visual order: 2nd, 1st, 3rd
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        order.forEach { idx ->
            if (idx < top3.size) PodiumSlot(top3[idx], idx + 1)
        }
    }
}

@Composable
fun PodiumSlot(player: Player, rank: Int) {
    val height  = when (rank) { 1 -> 120.dp; 2 -> 90.dp; else -> 70.dp }
    val medal   = when (rank) { 1 -> "🥇"; 2 -> "🥈"; else -> "🥉" }
    val color   = when (rank) { 1 -> Gold; 2 -> Color(0xFFB0BEC5); else -> Color(0xFFCD7F32) }

    val infiniteTransition = rememberInfiniteTransition(label = "podium")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = if (rank == 1) 1.05f else 1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "podium_glow"
    )

    Column(
        modifier = Modifier.width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(medal, fontSize = 24.sp, modifier = Modifier.scale(glowScale))
        Spacer(Modifier.height(4.dp))
        Text(
            player.displayName.take(8),
            color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp,
            maxLines = 1
        )
        Text("${player.totalScore} pts", color = WhiteGlow.copy(0.6f), fontSize = 11.sp)
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(Brush.verticalGradient(listOf(color.copy(0.3f), color.copy(0.1f))))
                .border(1.dp, color.copy(0.5f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("$rank", color = color, fontWeight = FontWeight.Black, fontSize = 22.sp)
        }
    }
}

@Composable
fun LeaderboardRow(rank: Int, player: Player) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(0.04f))
            .border(1.dp, WhiteGlow.copy(0.08f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "#$rank",
            color = WhiteGlow.copy(0.4f),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.width(36.dp)
        )
        Spacer(Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Gold.copy(0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(player.displayName.first().uppercaseChar().toString(), color = Gold, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(player.displayName, color = WhiteGlow, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("${player.municipalitiesUnlocked} municipalities", color = WhiteGlow.copy(0.4f), fontSize = 11.sp)
        }
        Text(
            "${player.totalScore}",
            color = Gold, fontWeight = FontWeight.Black, fontSize = 18.sp
        )
    }
}
