package com.example.lakbayaral.ui.mainmenu

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.lakbayaral.data.repository.LocalUserProfile
import com.example.lakbayaral.data.repository.UserProgressRepository
import com.example.lakbayaral.ui.components.*
import com.example.lakbayaral.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class MainMenuUiState(
    val currentUserId: String = "",
    val playerName: String = "Explorer",
    val totalScore: Int = 0,
    val unlockedCount: Int = 1,
    val users: List<LocalUserProfile> = emptyList()
)

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val progressRepo: UserProgressRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainMenuUiState())
    val uiState: StateFlow<MainMenuUiState> = _uiState

    init {
        refresh()
    }

    fun switchToUser(userId: String) {
        progressRepo.switchToUser(userId)
        refresh()
    }

    fun switchToUserName(name: String) {
        progressRepo.switchToUserName(name)
        refresh()
    }

    fun deleteUser(userId: String) {
        progressRepo.deleteUser(userId)
        refresh()
    }

    private fun refresh() {
        _uiState.value = MainMenuUiState(
            currentUserId = progressRepo.getCurrentUserId(),
            playerName = progressRepo.getPlayerName(),
            totalScore = progressRepo.getTotalScore(),
            unlockedCount = progressRepo.progress.unlockedMunicipalities.size,
            users = progressRepo.getLocalUsers()
        )
    }
}

@Composable
fun MainMenuScreen(
    onStartExploration: () -> Unit,
    onLeaderboard     : () -> Unit,
    viewModel         : MainMenuViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSwitchUserDialog by remember { mutableStateOf(false) }

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
                        Text(uiState.playerName, color = WhiteGlow, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("TOTAL SCORE", color = NeonBlue, fontSize = 10.sp, letterSpacing = 1.sp)
                        Text("${uiState.totalScore}", color = Gold, fontWeight = FontWeight.Black, fontSize = 22.sp)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PulsingDot(color = SuccessGreen, size = 8.dp)
                    Spacer(Modifier.width(6.dp))
                    Text("${uiState.unlockedCount} municipality unlocked", color = SuccessGreen, fontSize = 12.sp)
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
            Spacer(Modifier.height(12.dp))
            MenuButton(
                icon     = Icons.Default.Person,
                label    = "SWITCH USER",
                subtitle = "Change local explorer profile",
                onClick  = { showSwitchUserDialog = true }
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

    if (showSwitchUserDialog) {
        SwitchUserDialog(
            users = uiState.users,
            currentUserId = uiState.currentUserId,
            onSelectUser = { userId ->
                viewModel.switchToUser(userId)
                showSwitchUserDialog = false
            },
            onSwitchToName = { name ->
                viewModel.switchToUserName(name)
                showSwitchUserDialog = false
            },
            onDeleteUser = { userId ->
                viewModel.deleteUser(userId)
                showSwitchUserDialog = false
            },
            onDismiss = { showSwitchUserDialog = false }
        )
    }
}

@Composable
private fun SwitchUserDialog(
    users: List<LocalUserProfile>,
    currentUserId: String,
    onSelectUser: (String) -> Unit,
    onSwitchToName: (String) -> Unit,
    onDeleteUser: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var userName by remember { mutableStateOf("") }
    var selectedUserId by remember(users, currentUserId) {
        mutableStateOf(users.firstOrNull { it.userId == currentUserId }?.userId ?: users.firstOrNull()?.userId)
    }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val selectedUser = users.firstOrNull { it.userId == selectedUserId }
    val canSwitchByName = userName.isNotBlank()
    val canSwitchSelected = selectedUser != null && selectedUser.userId != currentUserId

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Switch User", color = Gold, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Local users", color = NeonBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 180.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    users.forEach { user ->
                        val selected = user.userId == selectedUserId
                        TextButton(
                            onClick = { selectedUserId = user.userId },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (selected) Gold.copy(alpha = 0.08f) else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(user.username, color = WhiteGlow, fontWeight = FontWeight.SemiBold)
                                    Text("${user.totalScore} pts", color = WhiteGlow.copy(alpha = 0.5f), fontSize = 11.sp)
                                }
                                if (user.userId == currentUserId) {
                                    Text("Current", color = SuccessGreen, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { selectedUser?.let { onSelectUser(it.userId) } },
                        enabled = canSwitchSelected
                    ) {
                        Text("Switch Selected", color = if (canSwitchSelected) Gold else WhiteGlow.copy(alpha = 0.35f))
                    }
                    TextButton(
                        onClick = { showDeleteConfirmation = true },
                        enabled = selectedUser != null
                    ) {
                        Text("Delete User", color = if (selectedUser != null) ErrorRed else WhiteGlow.copy(alpha = 0.35f))
                    }
                }

                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("User name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Gold,
                        unfocusedBorderColor = WhiteGlow.copy(alpha = 0.25f),
                        focusedTextColor = WhiteGlow,
                        unfocusedTextColor = WhiteGlow,
                        cursorColor = Gold
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSwitchToName(userName) },
                enabled = canSwitchByName
            ) {
                Text("Switch", color = if (canSwitchByName) Gold else WhiteGlow.copy(alpha = 0.35f))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = WhiteGlow.copy(alpha = 0.6f))
            }
        },
        containerColor = DarkBg,
        titleContentColor = Gold,
        textContentColor = WhiteGlow
    )

    if (showDeleteConfirmation && selectedUser != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete User", color = ErrorRed, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this user?", color = WhiteGlow) },
            confirmButton = {
                TextButton(onClick = { onDeleteUser(selectedUser.userId) }) {
                    Text("YES", color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("NO", color = WhiteGlow.copy(alpha = 0.7f))
                }
            },
            containerColor = DarkBg,
            titleContentColor = ErrorRed,
            textContentColor = WhiteGlow
        )
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
