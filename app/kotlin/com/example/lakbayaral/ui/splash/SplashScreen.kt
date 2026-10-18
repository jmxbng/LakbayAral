package com.example.lakbayaral.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lakbayaral.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    // Logo scale animation
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "scale"
    )

    // Glow pulse
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000), repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Subtitle fade
    var subtitleVisible by remember { mutableStateOf(false) }
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (subtitleVisible) 1f else 0f,
        animationSpec = tween(800),
        label = "subtitle"
    )

    LaunchedEffect(Unit) {
        delay(600)
        subtitleVisible = true
        delay(2200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0D1A30),
                        DarkBg
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background glow orbs
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Gold.copy(alpha = glowAlpha * 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App icon / emblem placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(scale)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Gold, Color(0xFF6B4A00))
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🏛️", fontSize = 40.sp)
            }

            Spacer(Modifier.height(24.dp))

            // Title
            Text(
                text       = "LAKBAY ARAL",
                fontSize   = 36.sp,
                fontWeight = FontWeight.Black,
                color      = Gold,
                letterSpacing = 4.sp,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.scale(scale)
            )

            Spacer(Modifier.height(8.dp))

            // Subtitle
            Text(
                text       = "Exploring History Through\nAugmented Reality",
                fontSize   = 14.sp,
                color      = NeonBlue.copy(alpha = subtitleAlpha),
                textAlign  = TextAlign.Center,
                letterSpacing = 1.sp,
                modifier   = Modifier.alpha(subtitleAlpha)
            )

            Spacer(Modifier.height(48.dp))

            // Loading dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { i ->
                    val dotAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.2f, targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = i * 200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot$i"
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                Gold.copy(alpha = dotAlpha),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                }
            }
        }

        // Bottom text
        Text(
            text     = "Pampanga Heritage Series",
            color    = WhiteGlow.copy(alpha = 0.3f),
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}
