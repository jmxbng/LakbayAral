package com.example.lakbayaral.ui.leaderboard

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.lakbayaral.data.repository.FirebaseRepository
import com.example.lakbayaral.data.repository.UserProgressRepository
import com.example.lakbayaral.ui.components.DarkBackground
import com.example.lakbayaral.ui.components.GameTopBar
import com.example.lakbayaral.ui.theme.Gold
import com.example.lakbayaral.ui.theme.NeonBlue
import com.example.lakbayaral.ui.theme.SuccessGreen
import com.example.lakbayaral.ui.theme.WhiteGlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val progressRepo: UserProgressRepository,
    private val firebaseRepo: FirebaseRepository
) : ViewModel() {

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mode = MutableStateFlow(LeaderboardMode.OFFLINE)
    val mode: StateFlow<LeaderboardMode> = _mode

    private val _statusMessage = MutableStateFlow("")
    val statusMessage: StateFlow<String> = _statusMessage
    private var onlinePlayersJob: Job? = null

    init {
        loadOfflineLeaderboard()
    }

    private fun loadOfflineLeaderboard() {
        viewModelScope.launch {
            _isLoading.value = true
            _players.value = progressRepo.getLeaderboardPlayers()
            _isLoading.value = false
        }
    }

    fun showOffline() {
        onlinePlayersJob?.cancel()
        onlinePlayersJob = null
        _mode.value = LeaderboardMode.OFFLINE
        _statusMessage.value = ""
        loadOfflineLeaderboard()
    }

    fun showOnline() {
        onlinePlayersJob?.cancel()
        _mode.value = LeaderboardMode.ONLINE_PVP
        _isLoading.value = true
        _statusMessage.value = if (firebaseRepo.isSignedIn()) "" else "Sign in online to sync your score."
        onlinePlayersJob = viewModelScope.launch {
            firebaseRepo.getTopPlayers()
                .catch { error ->
                    _players.value = emptyList()
                    _statusMessage.value = "Unable to load online rankings. ${error.message.orEmpty()}".trim()
                    _isLoading.value = false
                }
                .collect { onlinePlayers ->
                    _players.value = onlinePlayers
                    _isLoading.value = false
                }
        }
    }

    fun uploadMyScore() {
        viewModelScope.launch {
            if (_mode.value == LeaderboardMode.OFFLINE) {
                progressRepo.saveLocalLeaderboardEntry()
                loadOfflineLeaderboard()
                return@launch
            }

            progressRepo.syncCurrentProgress().onSuccess {
                _statusMessage.value = "Online score synced."
            }.onFailure { error ->
                _statusMessage.value = error.message ?: "Unable to sync online score."
            }
        }
    }
}

enum class LeaderboardMode { OFFLINE, ONLINE_PVP }

@Composable
fun LeaderboardScreen(
    onBack: () -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val players by viewModel.players.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val mode by viewModel.mode.collectAsState()
    val message by viewModel.statusMessage.collectAsState()
    val online = mode == LeaderboardMode.ONLINE_PVP

    DarkBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            GameTopBar(title = if (online) "ONLINE PVP RANKINGS" else "OFFLINE LEADERBOARD", onBack = onBack)

            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(selected = !online, onClick = viewModel::showOffline, label = { Text("Offline") })
                FilterChip(selected = online, onClick = viewModel::showOnline, label = { Text("Online PVP") })
                Spacer(Modifier.weight(1f))
                TextButton(onClick = viewModel::uploadMyScore) {
                    Text(if (online) "Sync Online" else "Refresh", color = NeonBlue, fontSize = 12.sp)
                }
            }

            if (message.isNotBlank()) {
                Text(
                    text = message,
                    color = if (message.contains("synced", ignoreCase = true)) SuccessGreen else Gold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Gold)
                }
            } else if (players.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No rankings yet", color = WhiteGlow.copy(0.5f))
                        Text(
                            if (online) "Sync your score after signing in online." else "Complete a quiz to appear here!",
                            color = WhiteGlow.copy(0.3f),
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        if (players.size >= 3) {
                            PodiumRow(top3 = players.take(3), online = online)
                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    itemsIndexed(players.drop(if (players.size >= 3) 3 else 0)) { index, player ->
                        LeaderboardRow(rank = index + 4, player = player, online = online)
                    }
                }
            }
        }
    }
}

@Composable
fun PodiumRow(top3: List<Player>, online: Boolean) {
    val order = if (top3.size >= 2) listOf(1, 0, 2) else listOf(0)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        order.forEach { idx ->
            if (idx < top3.size) PodiumSlot(top3[idx], idx + 1, online)
        }
    }
}

@Composable
fun PodiumSlot(player: Player, rank: Int, online: Boolean) {
    val height = when (rank) {
        1 -> 120.dp
        2 -> 90.dp
        else -> 70.dp
    }
    val color = when (rank) {
        1 -> Gold
        2 -> Color(0xFFB0BEC5)
        else -> Color(0xFFCD7F32)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "podium")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (rank == 1) 1.05f else 1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "podium_glow"
    )

    Column(
        modifier = Modifier.width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("#$rank", color = color, fontWeight = FontWeight.Black, fontSize = 22.sp, modifier = Modifier.scale(glowScale))
        Spacer(Modifier.height(4.dp))
        Text(
            player.displayName.take(8),
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            maxLines = 1
        )
        Text("${if (online) player.pvpPoints else player.totalScore} pts", color = WhiteGlow.copy(0.6f), fontSize = 11.sp)
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
fun LeaderboardRow(rank: Int, player: Player, online: Boolean = false) {
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
            Text(player.displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?", color = Gold, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(player.displayName, color = WhiteGlow, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(
                if (online) "${player.pvpPoints} PVP pts | ${player.municipalitiesUnlocked} municipalities" else "${player.municipalitiesUnlocked} municipalities",
                color = WhiteGlow.copy(0.4f),
                fontSize = 11.sp
            )
        }
        Text(
            "${if (online) player.pvpPoints else player.totalScore}",
            color = Gold,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp
        )
    }
}
