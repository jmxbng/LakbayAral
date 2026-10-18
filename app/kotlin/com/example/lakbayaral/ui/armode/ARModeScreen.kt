package com.example.lakbayaral.ui.armode

import android.Manifest
import android.content.pm.PackageManager
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.example.lakbayaral.ar.ARInteractionManager
import com.example.lakbayaral.ar.ARSpawner
import com.example.lakbayaral.data.models.HistoricalObject
import com.example.lakbayaral.data.models.Municipality
import com.example.lakbayaral.data.models.ObjectType
import com.example.lakbayaral.data.repository.LocalDataSource
import com.example.lakbayaral.data.repository.UserProgressRepository
import com.example.lakbayaral.ui.components.DarkBackground
import com.example.lakbayaral.ui.components.NeonButton
import com.example.lakbayaral.ui.components.PulsingDot
import com.example.lakbayaral.ui.theme.DarkBg
import com.example.lakbayaral.ui.theme.Gold
import com.example.lakbayaral.ui.theme.NeonBlue
import com.example.lakbayaral.ui.theme.SuccessGreen
import com.example.lakbayaral.ui.theme.WhiteGlow
import com.google.android.filament.ColorGrading
import com.google.android.filament.ToneMapper
import com.google.android.filament.utils.KTX1Loader
import com.google.ar.core.Config
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.romainguy.kotlin.math.Float2
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.math.Direction
import io.github.sceneview.node.Node
import io.github.sceneview.utils.readBuffer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

private const val NEUTRAL_IBL_ASSET = "environments/neutral/neutral_ibl.ktx"
private const val AR_OBJECT_IBL_INTENSITY = 36_000f
private const val AR_OBJECT_KEY_LIGHT_INTENSITY = 58_000f
private const val AR_OBJECT_EXPOSURE_APERTURE = 16.0f
private const val AR_OBJECT_EXPOSURE_SHUTTER = 1f / 125f
private const val AR_OBJECT_EXPOSURE_ISO = 100f
private const val AR_OBJECT_COLOR_EXPOSURE = 0.0f
private val AR_OBJECT_KEY_LIGHT_DIRECTION = Direction(x = -0.38f, y = -0.82f, z = -0.42f)

@HiltViewModel
class ARModeViewModel @Inject constructor(
    private val progressRepo: UserProgressRepository
) : ViewModel() {
    private var activeMunicipalityId: String = ""
    private var activeMode: String = ""

    private val _municipality = MutableStateFlow<Municipality?>(null)
    val municipality: StateFlow<Municipality?> = _municipality

    private val _accessAllowed = MutableStateFlow(false)
    val accessAllowed: StateFlow<Boolean> = _accessAllowed

    private val _selectedObject = MutableStateFlow<HistoricalObject?>(null)
    val selectedObject: StateFlow<HistoricalObject?> = _selectedObject

    private val _allPlaced = MutableStateFlow(false)
    val allPlaced: StateFlow<Boolean> = _allPlaced

    private val _placedCount = MutableStateFlow(0)
    val placedCount: StateFlow<Int> = _placedCount

    fun load(municipalityId: String, mode: String) {
        activeMunicipalityId = municipalityId
        activeMode = mode
        _accessAllowed.value = progressRepo.isMunicipalityUnlocked(municipalityId)
        _municipality.value = if (_accessAllowed.value) {
            LocalDataSource.getMunicipality(municipalityId)
        } else {
            null
        }
        _selectedObject.value = null
        _allPlaced.value = false
        _placedCount.value = 0
    }

    fun canAccessMunicipality(municipalityId: String): Boolean =
        progressRepo.isMunicipalityUnlocked(municipalityId)

    fun getObjectList(mode: String): List<HistoricalObject> {
        val m = _municipality.value ?: return emptyList()
        return if (mode == "places") m.historicalPlaces else m.historicalPeople
    }

    fun onObjectTapped(obj: HistoricalObject) {
        _selectedObject.value = obj
    }

    fun dismissPanel() {
        _selectedObject.value = null
    }

    fun onObjectPlaced(total: Int) {
        _placedCount.value = (_placedCount.value + 1).coerceAtMost(total)
        progressRepo.saveARProgress(
            municipalityId = activeMunicipalityId,
            mode = activeMode,
            placedCount = _placedCount.value,
            totalCount = total
        )
    }

    fun onAllPlaced() {
        _allPlaced.value = true
    }
}

@Composable
fun ARModeScreen(
    municipalityId: String,
    mode: String,
    onBack: () -> Unit,
    onGoToQuiz: () -> Unit,
    viewModel: ARModeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val accessAllowed by viewModel.accessAllowed.collectAsState()
    val municipality by viewModel.municipality.collectAsState()
    val selectedObj by viewModel.selectedObject.collectAsState()
    val allPlaced by viewModel.allPlaced.collectAsState()
    val placedCount by viewModel.placedCount.collectAsState()

    var cameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> cameraPermission = granted }

    LaunchedEffect(municipalityId, mode) {
        viewModel.load(municipalityId, mode)
        if (viewModel.canAccessMunicipality(municipalityId) && !cameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!accessAllowed) {
        ARAccessDeniedScreen(onBack)
        return
    }

    if (!cameraPermission) {
        PermissionDeniedScreen(onBack)
        return
    }

    val objectList = viewModel.getObjectList(mode)
    val modeLabel = if (mode == "places") "Historical Places" else "Historical People"

    Box(modifier = Modifier.fillMaxSize()) {
        if (municipality != null) {
            ARView(
                objectList = objectList,
                onObjectTapped = viewModel::onObjectTapped,
                onObjectPlaced = { viewModel.onObjectPlaced(objectList.size) },
                onAllPlaced = viewModel::onAllPlaced
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            ARTopHud(
                modeLabel = modeLabel,
                municipalityName = municipality?.name.orEmpty(),
                placedCount = placedCount,
                totalCount = objectList.size,
                onBack = onBack
            )

            Spacer(Modifier.weight(1f))

            if (!allPlaced) {
                PlacementPrompt(
                    nextObject = objectList.getOrNull(placedCount),
                    hasObjects = objectList.isNotEmpty()
                )
            }
        }

        AnimatedVisibility(
            visible = allPlaced && objectList.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            AllPlacedBanner(onGoToQuiz = onGoToQuiz)
        }

        AnimatedVisibility(
            visible = selectedObj != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selectedObj?.let { obj ->
                HistoryPanel(obj = obj, onDismiss = viewModel::dismissPanel)
            }
        }
    }
}

@Composable
private fun ARAccessDeniedScreen(onBack: () -> Unit) {
    DarkBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🔒", fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text("AR MODE LOCKED", color = Gold, fontWeight = FontWeight.Black, fontSize = 24.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "Unlock this municipality through the required quiz sequence before entering AR mode.",
                color = WhiteGlow.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))
            NeonButton("BACK TO MAP", onClick = onBack, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ARTopHud(
    modeLabel: String,
    municipalityName: String,
    placedCount: Int,
    totalCount: Int,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(DarkBg.copy(alpha = 0.72f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Text("<", color = Gold, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(modeLabel, color = Gold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(municipalityName, color = NeonBlue, fontSize = 11.sp)
        }
        Text(
            "$placedCount / $totalCount placed",
            color = WhiteGlow,
            fontSize = 12.sp,
            modifier = Modifier
                .background(NeonBlue.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun PlacementPrompt(nextObject: HistoricalObject?, hasObjects: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBg.copy(alpha = 0.78f))
            .navigationBarsPadding()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PulsingDot(color = NeonBlue)
            Spacer(Modifier.height(6.dp))
            if (nextObject != null) {
                Text("Tap a tracked floor plane to anchor", color = WhiteGlow.copy(alpha = 0.62f), fontSize = 12.sp)
                Text(nextObject.name, color = Gold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Then tap the hologram to open its history", color = WhiteGlow.copy(alpha = 0.46f), fontSize = 11.sp)
            } else if (!hasObjects) {
                Text("No AR objects configured for this mode", color = WhiteGlow.copy(alpha = 0.7f), fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun AllPlacedBanner(onGoToQuiz: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBg.copy(alpha = 0.92f))
            .navigationBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("All objects explored", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text("Tap any object to review its history", color = WhiteGlow.copy(alpha = 0.62f), fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        NeonButton(text = "TAKE THE QUIZ ->", onClick = onGoToQuiz, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun ARView(
    objectList: List<HistoricalObject>,
    onObjectTapped: (HistoricalObject) -> Unit,
    onObjectPlaced: () -> Unit,
    onAllPlaced: () -> Unit
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            ARSceneView(ctx).also { arView ->
                arView.configureSession { _, config ->
                    config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
                    config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                    config.lightEstimationMode = Config.LightEstimationMode.DISABLED
                }
                configureARObjectRendering(arView)

                val spawner = ARSpawner(ctx, arView, onObjectTapped)
                var activeRotationRoot: Node? = null
                val manager = ARInteractionManager(
                    objectList = objectList,
                    onPlacementTap = { obj, hit ->
                        val placed = spawner.spawnObject(obj, hit)
                        if (placed) {
                            onObjectPlaced()
                        }
                        placed
                    },
                    onOutOfObjects = onAllPlaced
                )

                arView.onGestureListener =
                    object : io.github.sceneview.gesture.GestureDetector.SimpleOnGestureListener() {
                        override fun onDown(e: MotionEvent, node: Node?) {
                            activeRotationRoot = spawner.findObjectRoot(node)
                            spawner.beginObjectRotation(activeRotationRoot)
                        }

                        override fun onScroll(
                            e1: MotionEvent?,
                            e2: MotionEvent,
                            node: Node?,
                            distance: Float2
                        ) {
                            val rotationRoot = activeRotationRoot ?: spawner.findObjectRoot(node)
                            if (rotationRoot != null) {
                                spawner.rotateObjectFromDrag(rotationRoot, distance.x)
                            }
                        }

                        override fun onMoveEnd(
                            detector: io.github.sceneview.gesture.MoveGestureDetector,
                            e: MotionEvent,
                            node: Node?
                        ) {
                            spawner.endObjectRotation(activeRotationRoot ?: node)
                            activeRotationRoot = null
                        }

                        override fun onSingleTapConfirmed(e: MotionEvent, node: Node?) {
                            if (node != null || manager.allPlaced) return

                            val camera = arView.frame?.camera ?: return
                            val hitResult = arView.hitTestAR(
                                xPx = e.x,
                                yPx = e.y,
                                planeTypes = setOf(Plane.Type.HORIZONTAL_UPWARD_FACING),
                                trackingStates = setOf(TrackingState.TRACKING),
                                planePoseInPolygon = true,
                                minCameraDistance = camera to 0.35f
                            ) ?: return

                            val plane = hitResult.trackable as? Plane ?: return
                            manager.onPlaneTap(hitResult, plane)
                        }
                    }
            }
        }
    )
}

private fun configureARObjectRendering(arView: ARSceneView) {
    arView.lightEstimator?.apply {
        isEnabled = false
        environmentalHdrReflections = false
        environmentalHdrSphericalHarmonics = false
        environmentalHdrSpecularFilter = false
        environmentalHdrMainLightDirection = false
        environmentalHdrMainLightIntensity = false
    }

    runCatching {
        KTX1Loader.createIndirectLight(
            arView.engine,
            arView.context.assets.readBuffer(NEUTRAL_IBL_ASSET)
        ).apply {
            setIntensity(AR_OBJECT_IBL_INTENSITY)
        }
    }.getOrNull()?.let { neutralIndirectLight ->
        arView.environment = arView.environmentLoader.createEnvironment(
            indirectLight = neutralIndirectLight,
            skybox = null
        )
    } ?: arView.indirectLight?.setIntensity(AR_OBJECT_IBL_INTENSITY)

    arView.mainLightNode?.apply {
        intensity = AR_OBJECT_KEY_LIGHT_INTENSITY
        lightDirection = AR_OBJECT_KEY_LIGHT_DIRECTION
        isShadowCaster = false
    }
    arView.cameraNode.setExposure(
        AR_OBJECT_EXPOSURE_APERTURE,
        AR_OBJECT_EXPOSURE_SHUTTER,
        AR_OBJECT_EXPOSURE_ISO
    )
    arView.view.colorGrading = ColorGrading.Builder()
        .toneMapper(ToneMapper.PBRNeutralToneMapper())
        .exposure(AR_OBJECT_COLOR_EXPOSURE)
        .contrast(1.0f)
        .saturation(1.0f)
        .build(arView.engine)
}

@Composable
fun HistoryPanel(obj: HistoricalObject, onDismiss: () -> Unit) {
    val typeLabel = if (obj.type == ObjectType.PLACE) "PLACE ARCHIVE" else "PEOPLE ARCHIVE"
    val infiniteTransition = rememberInfiniteTransition(label = "history_panel")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "panel_float"
    )
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "panel_scan"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 460.dp)
            .graphicsLayer { translationY = floatY }
            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xDD0B0F1A), Color(0xF20D1525))
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(listOf(Gold.copy(alpha = 0.72f), NeonBlue.copy(alpha = 0.5f))),
                shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
            )
            .drawBehind {
                val y = size.height * scanProgress
                drawLine(
                    color = NeonBlue.copy(alpha = 0.22f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    color = Gold.copy(alpha = 0.18f),
                    start = Offset(24.dp.toPx(), 0f),
                    end = Offset(size.width - 24.dp.toPx(), 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .navigationBarsPadding()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(WhiteGlow.copy(alpha = 0.22f))
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .widthIn(min = 88.dp)
                    .border(1.dp, NeonBlue.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                    .background(NeonBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(typeLabel, color = NeonBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(obj.name, color = Gold, fontWeight = FontWeight.Black, fontSize = 18.sp)
                if (obj.yearBuilt.isNotEmpty()) {
                    Text(obj.yearBuilt, color = WhiteGlow.copy(alpha = 0.68f), fontSize = 12.sp)
                }
            }

            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = WhiteGlow.copy(alpha = 0.68f))
            }
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = Gold.copy(alpha = 0.2f))
        Spacer(Modifier.height(12.dp))

        Text("HISTORY", color = NeonBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(obj.description, color = WhiteGlow.copy(alpha = 0.86f), fontSize = 14.sp, lineHeight = 22.sp)

        Spacer(Modifier.height(14.dp))

        Text("SIGNIFICANCE", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(obj.significance, color = WhiteGlow.copy(alpha = 0.76f), fontSize = 14.sp, lineHeight = 22.sp)

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
fun PermissionDeniedScreen(onBack: () -> Unit) {
    DarkBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(NeonBlue.copy(alpha = 0.13f))
                    .border(1.dp, NeonBlue.copy(alpha = 0.45f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("AR", color = NeonBlue, fontWeight = FontWeight.Black, fontSize = 20.sp)
            }
            Spacer(Modifier.height(16.dp))
            Text("Camera Permission Required", color = Gold, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "Lakbay Aral needs camera access to display AR historical objects.",
                color = WhiteGlow.copy(alpha = 0.66f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            NeonButton(text = "GO BACK", onClick = onBack, modifier = Modifier.fillMaxWidth())
        }
    }
}
