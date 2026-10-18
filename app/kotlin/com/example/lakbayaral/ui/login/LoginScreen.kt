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
import com.example.lakbayaral.data.models.OnlineUserProfile
import com.example.lakbayaral.data.repository.FirebaseRepository
import com.example.lakbayaral.data.repository.UserProgressRepository
import com.example.lakbayaral.ui.components.*
import com.example.lakbayaral.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── ViewModel ─────────────────────────────────────────────────────────────────
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val progressRepo: UserProgressRepository,
    private val firebaseRepo: FirebaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginState>(LoginState.Idle)
    val uiState: StateFlow<LoginState> = _uiState

    fun continueAsGuest(name: String) {
        viewModelScope.launch {
            _uiState.value = LoginState.Loading
            // A guest session must not upload progress into an account that was signed in
            // during an earlier session on the same device.
            firebaseRepo.signOut()
            progressRepo.setPlayerName(name.ifBlank { "Explorer" })
            delay(800) // UX delay
            _uiState.value = LoginState.Success
        }
    }

    fun registerOnline(username: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginState.Loading
            firebaseRepo.registerWithEmail(username.ifBlank { "Explorer" }, email, password)
                .onSuccess { profile ->
                    // A new account always receives the default Lubao-only
                    // state. Do not inherit an unrelated local profile that
                    // happens to have the same display name.
                    restoreOnlineProgress(profile, importActiveLocalProfile = false)
                }
                .onFailure { error ->
                    _uiState.value = LoginState.Error(firebaseErrorMessage(error, isRegistration = true))
                }
        }
    }

    fun loginOnline(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginState.Loading
            firebaseRepo.signInWithEmail(email, password)
                .onSuccess { profile ->
                    restoreOnlineProgress(profile, importActiveLocalProfile = true)
                }
                .onFailure { error ->
                    _uiState.value = LoginState.Error(firebaseErrorMessage(error))
                }
        }
    }

    private suspend fun restoreOnlineProgress(
        profile: OnlineUserProfile,
        importActiveLocalProfile: Boolean
    ) {
        progressRepo.restoreRemoteProgress(profile, importActiveLocalProfile)
            .onSuccess {
                _uiState.value = LoginState.Success
            }
            .onFailure { error ->
                _uiState.value = LoginState.Error(firebaseErrorMessage(error))
            }
    }

    private fun firebaseErrorMessage(error: Throwable, isRegistration: Boolean = false): String {
        val authCode = (error as? FirebaseAuthException)?.errorCode
        return when (authCode) {
            "ERROR_USER_NOT_FOUND" -> "Account does not exist."
            "ERROR_WRONG_PASSWORD" -> "Incorrect password."
            "ERROR_INVALID_LOGIN_CREDENTIALS", "ERROR_INVALID_CREDENTIAL" ->
                "Email or password is incorrect."
            "ERROR_USER_DISABLED" -> "This account has been disabled."
            "ERROR_INVALID_EMAIL" -> "Enter a valid email address."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "An account already exists for this email."
            "ERROR_WEAK_PASSWORD" -> "Use a stronger password."
            else -> when ((error as? FirebaseFirestoreException)?.code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                    if (isRegistration) {
                        "Account was created, but Firebase denied saving its profile. Check Firestore rules."
                    } else {
                        "Signed in, but Firebase denied access to your profile. Check Firestore rules."
                    }
                FirebaseFirestoreException.Code.UNAVAILABLE ->
                    "Unable to reach Firebase. Check your internet connection and try again."
                else -> if (isRegistration) {
                    "Unable to create the online account. ${error.message.orEmpty()}".trim()
                } else {
                    "Unable to sign in. ${error.message.orEmpty()}".trim()
                }
            }
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
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var onlineMode by remember { mutableStateOf(false) }
    var createAccount by remember { mutableStateOf(false) }

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
            Text(
                if (onlineMode) "Sign in for online PVP rankings" else "Enter your explorer name to begin",
                fontSize = 13.sp,
                color = WhiteGlow.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(40.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(if (onlineMode) "ONLINE ACCOUNT" else "EXPLORER PROFILE", color = NeonBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth()) {
                    FilterChip(selected = !onlineMode, onClick = { onlineMode = false }, label = { Text("Offline") }, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    FilterChip(selected = onlineMode, onClick = { onlineMode = true }, label = { Text("Online") }, modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value         = playerName,
                    onValueChange = { playerName = it },
                    label         = { Text(if (onlineMode) "Username" else "Your Name", color = WhiteGlow.copy(alpha = 0.6f)) },
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

                if (onlineMode) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = WhiteGlow.copy(alpha = 0.6f)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold,
                            unfocusedBorderColor = WhiteGlow.copy(alpha = 0.2f),
                            focusedTextColor = WhiteGlow,
                            unfocusedTextColor = WhiteGlow,
                            cursorColor = Gold
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = WhiteGlow.copy(alpha = 0.6f)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold,
                            unfocusedBorderColor = WhiteGlow.copy(alpha = 0.2f),
                            focusedTextColor = WhiteGlow,
                            unfocusedTextColor = WhiteGlow,
                            cursorColor = Gold
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = createAccount, onCheckedChange = { createAccount = it })
                        Text("Create new online account", color = WhiteGlow.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(24.dp))

                NeonButton(
                    text     = if (uiState is LoginState.Loading) "LOADING..." else if (onlineMode) "ONLINE SIGN IN" else "BEGIN JOURNEY",
                    onClick  = {
                        if (onlineMode && createAccount) viewModel.registerOnline(playerName, email, password)
                        else if (onlineMode) viewModel.loginOnline(email, password)
                        else viewModel.continueAsGuest(playerName)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled  = uiState !is LoginState.Loading
                )

                if (uiState is LoginState.Error) {
                    Spacer(Modifier.height(12.dp))
                    Text((uiState as LoginState.Error).msg, color = ErrorRed, fontSize = 12.sp)
                }
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
