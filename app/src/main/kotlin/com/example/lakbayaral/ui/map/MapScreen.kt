package com.example.lakbayaral.ui.map

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val progressRepo: UserProgressRepository
) : ViewModel() {
    val municipalities: List<Municipality>
        get() = LocalDataSource.municipalities.map { m ->
            m.copy(isUnlocked = progressRepo.isMunicipalityUnlocked(m.id))
        }
}

@Composable
fun MapScreen(
    onMunicipalitySelected: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val municipalities = viewModel.municipalities

    DarkBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            GameTopBar(title = "PAMPANGA — CHOOSE MUNICIPALITY", onBack = onBack)

            // Legend
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LegendItem(color = Gold, label = "Unlocked")
                LegendItem(color = WhiteGlow.copy(alpha = 0.3f), label = "Locked")
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(municipalities) { municipality ->
                    MunicipalityCard(
                        municipality = municipality,
                        onTap        = {
                            if (municipality.isUnlocked) onMunicipalitySelected(municipality.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MunicipalityCard(municipality: Municipality, onTap: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "card")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "glow"
    )

    val borderColor = if (municipality.isUnlocked)
        Brush.horizontalGradient(listOf(Gold, NeonBlue))
    else
        Brush.horizontalGradient(listOf(WhiteGlow.copy(0.1f), WhiteGlow.copy(0.1f)))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (municipality.isUnlocked)
                    Brush.verticalGradient(listOf(Color(0x22C8A24A), Color(0x110B0F1A)))
                else
                    Brush.verticalGradient(listOf(Color(0x11FFFFFF), Color(0x080B0F1A)))
            )
            .border(
                width  = if (municipality.isUnlocked) 1.5.dp else 1.dp,
                brush  = borderColor,
                shape  = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = municipality.isUnlocked, onClick = onTap)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Icon / number badge
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (municipality.isUnlocked)
                            Brush.radialGradient(listOf(Gold.copy(alpha = glowAlpha), Gold.copy(0.1f)))
                        else
                            Brush.radialGradient(listOf(WhiteGlow.copy(0.1f), Color.Transparent))
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (municipality.isUnlocked) {
                    Text("🗺️", fontSize = 26.sp)
                } else {
                    Icon(Icons.Default.Lock, contentDescription = "Locked", tint = WhiteGlow.copy(0.3f), modifier = Modifier.size(24.dp))
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    municipality.name,
                    color      = if (municipality.isUnlocked) Gold else WhiteGlow.copy(0.4f),
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp
                )
                Text(
                    municipality.province,
                    color    = if (municipality.isUnlocked) NeonBlue else WhiteGlow.copy(0.2f),
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text     = if (municipality.isUnlocked) municipality.description.take(80) + "..." else "Complete previous quiz to unlock",
                    color    = WhiteGlow.copy(alpha = if (municipality.isUnlocked) 0.6f else 0.3f),
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }

            Spacer(Modifier.width(8.dp))

            if (municipality.isUnlocked) {
                Text("›", color = Gold, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Text(label, color = WhiteGlow.copy(0.5f), fontSize = 11.sp)
    }
}
