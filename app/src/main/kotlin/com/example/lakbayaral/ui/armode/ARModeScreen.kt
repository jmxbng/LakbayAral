package com.example.lakbayaral.ui.armode

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lakbayaral.ar.ARInteractionManager
import com.example.lakbayaral.ar.ARSpawner
import com.example.lakbayaral.data.models.HistoricalObject
import com.example.lakbayaral.data.models.Municipality
import com.example.lakbayaral.data.models.ObjectType
import com.example.lakbayaral.data.repository.LocalDataSource
import com.example.lakbayaral.ui.components.*
import com.example.lakbayaral.ui.theme.*
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.node.Node
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ARModeScreen"

// ── ViewModel ─────────────────────────────────────────────────────────────────
@HiltViewModel
class ARModeViewModel @Inject constructor() : ViewModel() {
    private val _municipality = MutableStateFlow<Municipality?>(null)
    val municipality: StateFlow<Municipality?> = _municipality

    private val _selectedObject = MutableStateFlow<HistoricalObject?>(null)
    val selectedObject: StateFlow<HistoricalObject?> = _selectedObject

    private val _allPlaced = MutableStateFlow(false)
    val allPlaced: StateFlow<Boolean> = _allPlaced

    private val _placedCount = MutableStateFlow(0)
    val placedCount: StateFlow<Int> = _placedCount

    fun load(municipalityId: String, mode: String) {
        val m = LocalDataSource.getMunicipality(municipalityId) ?: return
        _municipality.value = m
    }

    fun getObjectList(mode: String): List<HistoricalObject> {
        val m = _municipality.value ?: return emptyList()
        return if (mode == "places") m.historicalPlaces else m.historicalPeople
    }

    fun onObjectTapped(obj: HistoricalObject) { _selectedObject.value = obj }
    fun dismissPanel() { _selectedObject.value = null }
    fun onObjectPlaced() { _placedCount.value++ }
    fun onAllPlaced() { _allPlaced.value = true }
}

// ── Screen ─────────────────────────────────────────────────────────────────────
@Composable
fun ARModeScreen(
    municipalityId : String,
    mode           : String,
    onBack         : () -> Unit,
    onGoToQuiz     : () -> Unit,
    viewModel      : ARModeViewModel = hiltViewModel()
) {
    val context      = LocalContext.current
    val municipality by viewModel.municipality.collectAsState()
    val selectedObj  by viewModel.selectedObject.collectAsState()
    val allPlaced    by viewModel.allPlaced.collectAsState()
    val placedCount  by viewModel.placedCount.collectAsState()

    var cameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> cameraPermission = granted }

    LaunchedEffect(municipalityId, mode) {
        viewModel.load(municipalityId, mode)
        if (!cameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (!cameraPermission) {
        PermissionDeniedScreen(onBack)
        return
    }

    val objectList = viewModel.getObjectList(mode)
    val modeLabel  = if (mode == "places") "Historical Places" else "Historical People"

    Box(modifier = Modifier.fillMaxSize()) {

        // ── AR View ───────────────────────────────────────────────────────────
        if (municipality != null) {
            ARView(
                objectList    = objectList,
                onObjectTapped = viewModel::onObjectTapped,
                onObjectPlaced = viewModel::onObjectPlaced,
                onAllPlaced    = viewModel::onAllPlaced
            )
        }

        // ── HUD overlay ───────────────────────────────────────────────────────
        Column(modifier = Modifier.fillMaxSize()) {

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(DarkBg.copy(alpha = 0.7f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Text("←", color = Gold, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(modeLabel, color = Gold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(municipality?.name ?: "", color = NeonBlue, fontSize = 11.sp)
                }
                Text(
                    "$placedCount / ${objectList.size} placed",
                    color = WhiteGlow, fontSize = 12.sp,
                    modifier = Modifier
                        .background(NeonBlue.copy(0.2f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            // Bottom instructions
            if (!allPlaced) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkBg.copy(alpha = 0.75f))
                        .navigationBarsPadding()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val nextObj = objectList.getOrNull(placedCount)
                        if (nextObj != null) {
                            PulsingDot(color = NeonBlue)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Tap a flat surface to place:",
                                color = WhiteGlow.copy(0.6f), fontSize = 12.sp
                            )
                            Text(
                                nextObj.name,
                                color = Gold, fontWeight = FontWeight.Bold, fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // ── All placed banner ─────────────────────────────────────────────────
        AnimatedVisibility(
            visible = allPlaced,
            enter   = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBg.copy(0.92f))
                    .navigationBarsPadding()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("✅ All objects explored!", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Tap any object to review its history", color = WhiteGlow.copy(0.6f), fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                NeonButton(text = "TAKE THE QUIZ →", onClick = onGoToQuiz, modifier = Modifier.fillMaxWidth())
            }
        }

        // ── History Panel ─────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = selectedObj != null,
            enter   = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit    = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selectedObj?.let { obj ->
                HistoryPanel(obj = obj, onDismiss = viewModel::dismissPanel)
            }
        }
    }
}

// ── AR SceneView wrapper ──────────────────────────────────────────────────────
@Composable
fun ARView(
    objectList     : List<HistoricalObject>,
    onObjectTapped : (HistoricalObject) -> Unit,
    onObjectPlaced : () -> Unit,
    onAllPlaced    : () -> Unit
) {
    val context = LocalContext.current

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory  = { ctx ->
            ARSceneView(ctx).also { arView ->
                val spawner = ARSpawner(context, arView, onObjectTapped)
                val manager = ARInteractionManager(
                    objectList      = objectList,
                    onObjectTap     = onObjectTapped,
                    onPlacementTap  = { obj, hit ->
                        spawner.spawnObject(obj, hit)
                        onObjectPlaced()
                    },
                    onOutOfObjects  = onAllPlaced
                )

                arView.onGestureListener = object : io.github.sceneview.gesture.GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapConfirmed(e: MotionEvent, node: Node?) {
                        val hitResult = arView.hitTestAR(e.x, e.y) ?: return
                        val plane = hitResult.trackable as? Plane ?: return
                        if (plane.trackingState == com.google.ar.core.TrackingState.TRACKING) {
                            manager.onPlaneTap(hitResult, plane)
                        }
                    }
                }
            }
        }
    )
}

// ── Holographic History Panel ─────────────────────────────────────────────────
@Composable
fun HistoryPanel(obj: HistoricalObject, onDismiss: () -> Unit) {
    val typeEmoji = if (obj.type == ObjectType.PLACE) "🏛️" else "👤"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xCC0B0F1A),
                        Color(0xEE0D1525)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(listOf(Gold.copy(0.6f), NeonBlue.copy(0.4f))),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .navigationBarsPadding()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Handle bar
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(WhiteGlow.copy(0.2f))
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(typeEmoji, fontSize = 28.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(obj.name, color = Gold, fontWeight = FontWeight.Black, fontSize = 18.sp)
                if (obj.yearBuilt.isNotEmpty()) {
                    Text(obj.yearBuilt, color = NeonBlue, fontSize = 12.sp)
                }
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = WhiteGlow.copy(0.6f))
            }
        }

        Spacer(Modifier.height(12.dp))
        Divider(color = Gold.copy(0.2f))
        Spacer(Modifier.height(12.dp))

        Text("HISTORY", color = NeonBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(Modifier.height(6.dp))
        Text(obj.description, color = WhiteGlow.copy(0.85f), fontSize = 14.sp, lineHeight = 22.sp)

        Spacer(Modifier.height(12.dp))

        Text("SIGNIFICANCE", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(Modifier.height(6.dp))
        Text(obj.significance, color = WhiteGlow.copy(0.75f), fontSize = 14.sp, lineHeight = 22.sp)

        Spacer(Modifier.height(24.dp))
    }
}

// ── Permission denied fallback ────────────────────────────────────────────────
@Composable
fun PermissionDeniedScreen(onBack: () -> Unit) {
    DarkBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("📷", fontSize = 48.sp)
            Spacer(Modifier.height(16.dp))
            Text("Camera Permission Required", color = Gold, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "Lakbay Aral needs camera access to display AR historical objects.",
                color = WhiteGlow.copy(0.6f), fontSize = 14.sp
            )
            Spacer(Modifier.height(24.dp))
            NeonButton(text = "GO BACK", onClick = onBack, modifier = Modifier.fillMaxWidth())
        }
    }
}
