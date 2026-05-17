package com.example.lakbayaral.ui.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lakbayaral.data.repository.UserProgressRepository
import com.example.lakbayaral.ui.components.*
import com.example.lakbayaral.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── ViewModel ─────────────────────────────────────────────────────────────────
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val progressRepo: UserProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginState>(LoginState.Idle)
    val uiState: StateFlow<LoginState> = _uiState

    fun continueAsGuest(name: String) {
        viewModelScope.launch {
            _uiState.value = LoginState.Loading
            progressRepo.setPlayerName(name.ifBlank { "Explorer" })
            delay(800) // UX delay
            _uiState.value = LoginState.Success
        }
    }
}

sealed class LoginState {
    object Idle    : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val msg: String) : LoginState()
}

// ── Screen ─────────────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var playerName by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is LoginState.Success) onLoginSuccess()
    }

    DarkBackground {
        // Decorative background orb
        Box(
            modifier = Modifier
                .size(350.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-80).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonBlue.copy(alpha = 0.06f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text("LAKBAY ARAL", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Gold, letterSpacing = 3.sp)
            Spacer(Modifier.height(4.dp))
            Text("Enter your explorer name to begin", fontSize = 13.sp, color = WhiteGlow.copy(alpha = 0.6f))

            Spacer(Modifier.height(40.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("EXPLORER PROFILE", color = NeonBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value         = playerName,
                    onValueChange = { playerName = it },
                    label         = { Text("Your Name", color = WhiteGlow.copy(alpha = 0.6f)) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Gold,
                        unfocusedBorderColor = WhiteGlow.copy(alpha = 0.2f),
                        focusedTextColor     = WhiteGlow,
                        unfocusedTextColor   = WhiteGlow,
                        cursorColor          = Gold
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(Modifier.height(24.dp))

                NeonButton(
                    text     = if (uiState is LoginState.Loading) "LOADING..." else "BEGIN JOURNEY",
                    onClick  = { viewModel.continueAsGuest(playerName) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled  = uiState !is LoginState.Loading
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text      = "No account required · Your progress is saved locally",
                fontSize  = 11.sp,
                color     = WhiteGlow.copy(alpha = 0.3f),
                textAlign = TextAlign.Center
            )
        }
    }
}
