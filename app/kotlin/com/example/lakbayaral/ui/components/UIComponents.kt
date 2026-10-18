package com.example.lakbayaral.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lakbayaral.ui.theme.*

// ── Glassmorphism Card ─────────────────────────────────────────────────────────
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = Gold.copy(alpha = 0.5f),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.White.copy(alpha = 0.04f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(borderColor, NeonBlue.copy(alpha = 0.3f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        content = content
    )
}

// ── Neon Glow Button ───────────────────────────────────────────────────────────
@Composable
fun NeonButton(
    text       : String,
    onClick    : () -> Unit,
    modifier   : Modifier = Modifier,
    glowColor  : Color = Gold,
    enabled    : Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .drawBehind {
                drawCircle(
                    color  = glowColor.copy(alpha = glowAlpha * 0.3f),
                    radius = size.maxDimension * 0.6f
                )
            },
        colors = ButtonDefaults.buttonColors(
            containerColor = glowColor.copy(alpha = 0.15f),
            contentColor   = glowColor
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(listOf(glowColor, NeonBlue))
        ),
        shape  = RoundedCornerShape(12.dp)
    ) {
        Text(
            text       = text,
            fontWeight = FontWeight.Bold,
            fontSize   = 16.sp,
            letterSpacing = 1.sp
        )
    }
}

// ── Section Title with Gold Accent ────────────────────────────────────────────
@Composable
fun GoldTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text       = text,
        style      = MaterialTheme.typography.displayLarge,
        textAlign  = TextAlign.Center,
        modifier   = modifier
    )
}

// ── Animated pulsing dot indicator ────────────────────────────────────────────
@Composable
fun PulsingDot(
    color: Color = NeonBlue,
    size: Dp = 12.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dot")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_scale"
    )
    Box(
        modifier = Modifier
            .size(size * scale)
            .clip(RoundedCornerShape(50))
            .background(color)
    )
}

// ── Top bar with back button ──────────────────────────────────────────────────
@Composable
fun GameTopBar(title: String, onBack: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Text("←", color = Gold, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text       = title,
            style      = MaterialTheme.typography.titleLarge,
            color      = Gold,
            fontWeight = FontWeight.Bold
        )
    }
}

// ── Cinematic dark gradient background ────────────────────────────────────────
@Composable
fun DarkBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBg, Color(0xFF0D1525), DarkBg)
                )
            ),
        content = content
    )
}
