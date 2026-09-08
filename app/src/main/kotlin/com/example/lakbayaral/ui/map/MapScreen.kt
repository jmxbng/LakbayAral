package com.example.lakbayaral.ui.map

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.example.lakbayaral.data.models.Municipality
import com.example.lakbayaral.data.repository.LocalDataSource
import com.example.lakbayaral.data.repository.UserProgressRepository
import com.example.lakbayaral.ui.components.DarkBackground
import com.example.lakbayaral.ui.components.GameTopBar
import com.example.lakbayaral.ui.components.NeonButton
import com.example.lakbayaral.ui.theme.DarkCard
import com.example.lakbayaral.ui.theme.DarkSurface
import com.example.lakbayaral.ui.theme.Gold
import com.example.lakbayaral.ui.theme.GoldLight
import com.example.lakbayaral.ui.theme.NeonBlue
import com.example.lakbayaral.ui.theme.SuccessGreen
import com.example.lakbayaral.ui.theme.WhiteGlow
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.InputStreamReader
import java.util.Locale
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@HiltViewModel
class MapViewModel @Inject constructor(
    private val progressRepo: UserProgressRepository
) : ViewModel() {
    val municipalities: List<Municipality>
        get() = LocalDataSource.municipalities.map { municipality ->
            municipality.copy(isUnlocked = progressRepo.isMunicipalityUnlocked(municipality.id))
        }
}

@Composable
fun MapScreen(
    onMunicipalitySelected: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val municipalities = viewModel.municipalities
    val mapData = rememberPampangaMapData(municipalities)
    var selectedId by remember(municipalities) {
        mutableStateOf(municipalities.firstOrNull { it.isUnlocked }?.id ?: municipalities.firstOrNull()?.id)
    }
    val selectedMunicipality = municipalities.firstOrNull { it.id == selectedId }
        ?: municipalities.firstOrNull { it.isUnlocked }

    DarkBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            GameTopBar(title = "EXPLORE PAMPANGA", onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                MapHeader(
                    unlockedCount = municipalities.count { it.isUnlocked },
                    totalCount = municipalities.size
                )

                if (mapData != null && mapData.regions.isNotEmpty()) {
                    PampangaMap(
                        mapData = mapData,
                        selectedId = selectedId,
                        onFocusChanged = { selectedId = it }
                    )
                } else {
                    DestinationFallbackList(
                        municipalities = municipalities,
                        selectedId = selectedId,
                        onSelect = { selectedId = it }
                    )
                }

                MapLegend()

                selectedMunicipality?.let { municipality ->
                    DestinationPreview(
                        municipality = municipality,
                        onOpen = { onMunicipalitySelected(municipality.id) }
                    )
                }

                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun MapHeader(unlockedCount: Int, totalCount: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Choose a destination and begin your Lakbay-Aral journey.",
            color = WhiteGlow.copy(alpha = 0.72f),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MapStatPill(
                label = "Unlocked",
                value = "$unlockedCount/$totalCount",
                color = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
            MapStatPill(
                label = "Map Mode",
                value = "Explore",
                color = Gold,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MapStatPill(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .heightIn(min = 44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = WhiteGlow.copy(alpha = 0.55f), fontSize = 11.sp)
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
private fun PampangaMap(
    mapData: PampangaMapData,
    selectedId: String?,
    onFocusChanged: (String) -> Unit
) {
    var hoveredId by remember { mutableStateOf<String?>(null) }
    var scale by remember { mutableFloatStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    val activeId = hoveredId ?: selectedId
    val activeRegion = mapData.regions.firstOrNull { it.id == activeId }
    val selectedRegion = mapData.regions.firstOrNull { it.id == selectedId }
    val hoveredRegion = mapData.regions.firstOrNull { it.id == hoveredId }
    val pulse by rememberInfiniteTransition(label = "map_pulse").animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "map_pulse_alpha"
    )
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        val nextScale = (scale * zoomChange).coerceIn(1f, 3f)
        scale = nextScale
        pan = if (nextScale <= 1.01f) {
            Offset.Zero
        } else {
            (pan + panChange).coerceForMap(canvasSize, nextScale)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(mapData.aspectRatio)
            .heightIn(min = 330.dp, max = 620.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF142034), Color(0xFF0C1424), Color(0xFF101A2D))
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(listOf(Gold.copy(alpha = 0.55f), NeonBlue.copy(alpha = 0.35f))),
                shape = RoundedCornerShape(18.dp)
            )
            .onSizeChanged { canvasSize = it.toSize() }
            .pointerInput(mapData, scale, pan, canvasSize) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val position = event.changes.firstOrNull()?.position ?: continue
                        hoveredId = when (event.type) {
                            PointerEventType.Move,
                            PointerEventType.Enter -> {
                                val frame = fittedMapFrame(canvasSize, mapData.aspectRatio)
                                findRegionAt(position, mapData, frame, scale, pan)?.id
                            }
                            PointerEventType.Exit -> null
                            else -> hoveredId
                        }
                    }
                }
            }
            .pointerInput(mapData, scale, pan, canvasSize) {
                detectTapGestures { tap ->
                    val frame = fittedMapFrame(canvasSize, mapData.aspectRatio)
                    val region = findRegionAt(tap, mapData, frame, scale, pan) ?: return@detectTapGestures
                    onFocusChanged(region.id)
                }
            }
            .transformable(transformState)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val frame = fittedMapFrame(size, mapData.aspectRatio)
            drawMapBackdrop(frame)

            clipRect(frame.left, frame.top, frame.right, frame.bottom) {
                withTransform({
                    translate(left = pan.x, top = pan.y)
                    scale(scaleX = scale, scaleY = scale, pivot = frameCenter(frame))
                }) {
                    drawDecorativeRoute(frame)

                    mapData.regions
                        .filterNot { it.id == selectedId || it.id == hoveredId }
                        .forEach { region ->
                            drawMunicipalityRegion(
                                region = region,
                                frame = frame,
                                active = false,
                                pulse = pulse
                            )
                        }

                    selectedRegion?.let { region ->
                        drawMunicipalityRegion(
                            region = region,
                            frame = frame,
                            active = true,
                            pulse = pulse
                        )
                    }

                    if (hoveredRegion != null && hoveredRegion.id != selectedId) {
                        drawMunicipalityRegion(
                            region = hoveredRegion,
                            frame = frame,
                            active = true,
                            pulse = pulse
                        )
                    }
                }
            }
        }

        MapPins(
            mapData = mapData,
            selectedId = activeId,
            canvasSize = canvasSize,
            scale = scale,
            pan = pan
        )

        MapLabels(
            mapData = mapData,
            selectedId = selectedId,
            hoveredId = hoveredId,
            canvasSize = canvasSize,
            scale = scale,
            pan = pan
        )

        MapControls(
            scale = scale,
            onZoomIn = { scale = (scale + 0.35f).coerceAtMost(3f) },
            onZoomOut = {
                scale = (scale - 0.35f).coerceAtLeast(1f)
                if (scale <= 1.01f) pan = Offset.Zero
            },
            onReset = {
                scale = 1f
                pan = Offset.Zero
            }
        )

        activeRegion?.let { region ->
            LocationTooltip(
                region = region,
                canvasSize = canvasSize,
                frame = fittedMapFrame(canvasSize, mapData.aspectRatio),
                scale = scale,
                pan = pan
            )
        }
    }
}

@Composable
private fun BoxScope.MapControls(
    scale: Float,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(10.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface.copy(alpha = 0.86f))
            .border(1.dp, WhiteGlow.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MapIconButton(label = "-", onClick = onZoomOut)
        Text(
            text = "${(scale * 100).roundToInt()}%",
            color = WhiteGlow.copy(alpha = 0.72f),
            fontSize = 10.sp,
            modifier = Modifier.width(38.dp),
            textAlign = TextAlign.Center
        )
        MapIconButton(label = "+", onClick = onZoomIn)
        AnimatedVisibility(visible = scale > 1.01f) {
            MapIconButton(label = "1x", onClick = onReset)
        }
    }
}

@Composable
private fun MapIconButton(label: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(30.dp)
    ) {
        Text(
            text = label,
            color = Gold,
            fontSize = if (label == "1x") 11.sp else 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BoxScope.MapPins(
    mapData: PampangaMapData,
    selectedId: String?,
    canvasSize: Size,
    scale: Float,
    pan: Offset
) {
    if (canvasSize.width <= 0f || canvasSize.height <= 0f) return

    val density = LocalDensity.current
    val frame = fittedMapFrame(canvasSize, mapData.aspectRatio)
    val pinHalf = with(density) { 7.dp.toPx() }

    mapData.regions.forEach { region ->
        val position = mapToCanvas(region.label, frame, scale, pan)
        val isSelected = region.id == selectedId
        val color = when {
            isSelected && region.municipality.isUnlocked -> GoldLight
            region.municipality.isUnlocked -> Gold
            else -> WhiteGlow.copy(alpha = 0.46f)
        }
        val pinScale by animateFloatAsState(
            targetValue = if (isSelected) 1.24f else 1f,
            animationSpec = tween(180, easing = FastOutSlowInEasing),
            label = "pin_scale_${region.id}"
        )

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (position.x - pinHalf).roundToInt(),
                        y = (position.y - pinHalf).roundToInt()
                    )
                }
                .size((14 * pinScale).dp)
                .clip(CircleShape)
                .background(DarkSurface.copy(alpha = 0.9f))
                .border(1.dp, color.copy(alpha = 0.8f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (region.municipality.isUnlocked) Icons.Default.LocationOn else Icons.Default.Lock,
                contentDescription = if (region.municipality.isUnlocked) {
                    "${region.municipality.name} unlocked"
                } else {
                    "${region.municipality.name} locked"
                },
                tint = color,
                modifier = Modifier.size(9.dp)
            )
        }
    }
}

@Composable
private fun BoxScope.MapLabels(
    mapData: PampangaMapData,
    selectedId: String?,
    hoveredId: String?,
    canvasSize: Size,
    scale: Float,
    pan: Offset
) {
    if (canvasSize.width <= 0f || canvasSize.height <= 0f) return

    val density = LocalDensity.current
    val frame = fittedMapFrame(canvasSize, mapData.aspectRatio)

    mapData.regions.forEach { region ->
        val position = mapToCanvas(region.label, frame, scale, pan)
        val labelOffset = PAMPANGA_LABEL_OFFSETS[region.id] ?: MapLabelOffset()
        val widthPx = with(density) { labelOffset.widthDp.dp.toPx() }
        val xOffsetPx = with(density) { labelOffset.xDp.dp.toPx() }
        val yOffsetPx = with(density) { labelOffset.yDp.dp.toPx() }
        val x = (position.x + xOffsetPx - widthPx / 2f)
            .coerceIn(4f, max(4f, canvasSize.width - widthPx - 4f))
        val y = (position.y + yOffsetPx)
            .coerceIn(4f, max(4f, canvasSize.height - 26f))
        val isSelected = region.id == selectedId
        val isHovered = region.id == hoveredId
        val isFocused = isSelected || isHovered
        val labelColor = when {
            region.municipality.isUnlocked && isFocused -> GoldLight
            region.municipality.isUnlocked -> Gold.copy(alpha = 0.88f)
            isFocused -> WhiteGlow.copy(alpha = 0.82f)
            else -> WhiteGlow.copy(alpha = 0.52f)
        }
        val borderColor = when {
            isSelected && region.municipality.isUnlocked -> NeonBlue.copy(alpha = 0.62f)
            isSelected -> WhiteGlow.copy(alpha = 0.35f)
            else -> Color.Transparent
        }

        Text(
            text = region.municipality.name,
            modifier = Modifier
                .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
                .width(labelOffset.widthDp.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(DarkSurface.copy(alpha = if (isFocused) 0.78f else 0.58f))
                .border(1.dp, borderColor, RoundedCornerShape(6.dp))
                .padding(horizontal = 3.dp, vertical = 2.dp),
            color = labelColor,
            fontSize = 7.sp,
            lineHeight = 8.sp,
            fontWeight = if (isFocused) FontWeight.Bold else FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BoxScope.LocationTooltip(
    region: PampangaMapRegion,
    canvasSize: Size,
    frame: Rect,
    scale: Float,
    pan: Offset
) {
    if (canvasSize.width <= 0f || canvasSize.height <= 0f) return

    val density = LocalDensity.current
    val position = mapToCanvas(region.label, frame, scale, pan)
    val tooltipWidth = with(density) { 178.dp.toPx() }
    val tooltipHeight = with(density) { 58.dp.toPx() }
    val x = (position.x - tooltipWidth / 2f).coerceIn(8f, max(8f, canvasSize.width - tooltipWidth - 8f))
    val y = (position.y - tooltipHeight - 14f).coerceIn(8f, max(8f, canvasSize.height - tooltipHeight - 8f))
    val statusColor = if (region.municipality.isUnlocked) SuccessGreen else WhiteGlow.copy(alpha = 0.48f)

    Column(
        modifier = Modifier
            .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
            .width(178.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface.copy(alpha = 0.94f))
            .border(1.dp, statusColor.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = region.municipality.name,
            color = if (region.municipality.isUnlocked) GoldLight else WhiteGlow.copy(alpha = 0.72f),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = if (region.municipality.isUnlocked) "Available to explore" else "This destination is locked.",
            color = statusColor,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MapLegend() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.045f))
            .border(1.dp, WhiteGlow.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        LegendItem(
            color = Gold,
            title = "Unlocked",
            description = "Available to explore",
            modifier = Modifier.weight(1f)
        )
        LegendItem(
            color = WhiteGlow.copy(alpha = 0.42f),
            title = "Locked",
            description = "Pass quizzes to unlock",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LegendItem(color: Color, title: String, description: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(13.dp)
                .clip(CircleShape)
                .background(color)
        )
        Column {
            Text(title, color = WhiteGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(description, color = WhiteGlow.copy(alpha = 0.5f), fontSize = 10.sp, maxLines = 1)
        }
    }
}

@Composable
private fun DestinationPreview(municipality: Municipality, onOpen: () -> Unit) {
    val borderColor = if (municipality.isUnlocked) Gold.copy(alpha = 0.55f) else WhiteGlow.copy(alpha = 0.14f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        if (municipality.isUnlocked) Gold.copy(alpha = 0.09f) else Color.White.copy(alpha = 0.035f),
                        DarkCard.copy(alpha = 0.78f)
                    )
                )
            )
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (municipality.isUnlocked) Gold.copy(alpha = 0.14f) else WhiteGlow.copy(alpha = 0.08f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (municipality.isUnlocked) Icons.Default.LocationOn else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (municipality.isUnlocked) Gold else WhiteGlow.copy(alpha = 0.42f),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = municipality.name,
                    color = if (municipality.isUnlocked) GoldLight else WhiteGlow.copy(alpha = 0.62f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (municipality.isUnlocked) "Destination unlocked" else "Destination locked",
                    color = if (municipality.isUnlocked) SuccessGreen else WhiteGlow.copy(alpha = 0.42f),
                    fontSize = 11.sp
                )
            }
        }

        Text(
            text = if (municipality.isUnlocked) {
                municipality.description
            } else {
                "Unlock this destination by passing another municipality quiz."
            },
            color = WhiteGlow.copy(alpha = if (municipality.isUnlocked) 0.74f else 0.46f),
            fontSize = 13.sp,
            lineHeight = 19.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DestinationMetric("${municipality.historicalPlaces.size}", "Places", Modifier.weight(1f))
            DestinationMetric("${municipality.historicalPeople.size}", "People", Modifier.weight(1f))
            DestinationMetric("${municipality.quiz.size}", "Quiz", Modifier.weight(1f))
        }

        if (municipality.isUnlocked) {
            NeonButton(
                text = "OPEN DESTINATION",
                onClick = onOpen,
                modifier = Modifier.fillMaxWidth(),
                glowColor = Gold
            )
        } else {
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = WhiteGlow.copy(alpha = 0.06f),
                    disabledContentColor = WhiteGlow.copy(alpha = 0.34f)
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("LOCKED", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DestinationMetric(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.045f))
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = NeonBlue, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(label, color = WhiteGlow.copy(alpha = 0.5f), fontSize = 10.sp)
    }
}

@Composable
private fun DestinationFallbackList(
    municipalities: List<Municipality>,
    selectedId: String?,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard.copy(alpha = 0.72f))
            .border(1.dp, Gold.copy(alpha = 0.28f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        municipalities.forEach { municipality ->
            val selected = municipality.id == selectedId
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = if (municipality.isUnlocked) 0.06f else 0.025f))
                    .border(
                        width = 1.dp,
                        color = if (selected) Gold.copy(alpha = 0.5f) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(municipality.id) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (municipality.isUnlocked) Icons.Default.LocationOn else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (municipality.isUnlocked) Gold else WhiteGlow.copy(alpha = 0.35f)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = municipality.name,
                    color = if (municipality.isUnlocked) WhiteGlow else WhiteGlow.copy(alpha = 0.42f),
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (selected) "SELECTED" else if (municipality.isUnlocked) "OPEN BELOW" else "LOCKED",
                    color = if (selected) Gold else WhiteGlow.copy(alpha = 0.42f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun DrawScope.drawMapBackdrop(frame: Rect) {
    drawRoundRect(
        color = Color(0xFF0A1726),
        topLeft = Offset(frame.left, frame.top),
        size = Size(frame.width, frame.height),
        cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx())
    )
    drawCircle(
        color = NeonBlue.copy(alpha = 0.10f),
        radius = frame.width * 0.28f,
        center = Offset(frame.left + frame.width * 0.15f, frame.top + frame.height * 0.14f)
    )
    drawCircle(
        color = Gold.copy(alpha = 0.08f),
        radius = frame.width * 0.30f,
        center = Offset(frame.right - frame.width * 0.16f, frame.bottom - frame.height * 0.12f)
    )
}

private fun DrawScope.drawDecorativeRoute(frame: Rect) {
    val route = Path().apply {
        moveTo(frame.left + frame.width * 0.12f, frame.top + frame.height * 0.78f)
        cubicTo(
            frame.left + frame.width * 0.28f,
            frame.top + frame.height * 0.66f,
            frame.left + frame.width * 0.42f,
            frame.top + frame.height * 0.78f,
            frame.left + frame.width * 0.58f,
            frame.top + frame.height * 0.62f
        )
        cubicTo(
            frame.left + frame.width * 0.74f,
            frame.top + frame.height * 0.46f,
            frame.left + frame.width * 0.82f,
            frame.top + frame.height * 0.35f,
            frame.left + frame.width * 0.88f,
            frame.top + frame.height * 0.18f
        )
    }

    drawPath(
        path = route,
        color = NeonBlue.copy(alpha = 0.13f),
        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawMunicipalityRegion(
    region: PampangaMapRegion,
    frame: Rect,
    active: Boolean,
    pulse: Float
) {
    val path = region.toPath(frame)
    val unlocked = region.municipality.isUnlocked
    val fillColor = when {
        active && unlocked -> GoldLight.copy(alpha = 0.76f)
        active -> WhiteGlow.copy(alpha = 0.28f)
        unlocked -> Gold.copy(alpha = 0.52f)
        else -> WhiteGlow.copy(alpha = 0.15f)
    }
    val strokeColor = when {
        active && unlocked -> NeonBlue.copy(alpha = 0.86f)
        active -> WhiteGlow.copy(alpha = 0.55f)
        unlocked -> GoldLight.copy(alpha = 0.55f)
        else -> WhiteGlow.copy(alpha = 0.22f)
    }

    if (active) {
        drawPath(
            path = path,
            color = strokeColor.copy(alpha = 0.24f + pulse * 0.20f),
            style = Stroke(width = 7.dp.toPx())
        )
    }

    drawPath(path = path, color = fillColor)
    drawPath(
        path = path,
        color = strokeColor,
        style = Stroke(width = if (active) 2.2.dp.toPx() else 1.1.dp.toPx())
    )
}

private fun fittedMapFrame(size: Size, aspectRatio: Float): Rect {
    if (size.width <= 0f || size.height <= 0f) return Rect(0f, 0f, 0f, 0f)
    val inset = min(size.width, size.height) * 0.055f
    val availableWidth = max(1f, size.width - inset * 2f)
    val availableHeight = max(1f, size.height - inset * 2f)
    val availableAspect = availableWidth / availableHeight
    val frameWidth: Float
    val frameHeight: Float

    if (availableAspect > aspectRatio) {
        frameHeight = availableHeight
        frameWidth = frameHeight * aspectRatio
    } else {
        frameWidth = availableWidth
        frameHeight = frameWidth / aspectRatio
    }

    val left = (size.width - frameWidth) / 2f
    val top = (size.height - frameHeight) / 2f
    return Rect(left, top, left + frameWidth, top + frameHeight)
}

private fun findRegionAt(
    position: Offset,
    mapData: PampangaMapData,
    frame: Rect,
    scale: Float,
    pan: Offset
): PampangaMapRegion? {
    if (frame.width <= 0f || frame.height <= 0f) return null

    val center = frameCenter(frame)
    val untransformed = Offset(
        x = ((position.x - pan.x - center.x) / scale) + center.x,
        y = ((position.y - pan.y - center.y) / scale) + center.y
    )
    if (!frameContains(frame, untransformed)) return null

    val normalized = Offset(
        x = ((untransformed.x - frame.left) / frame.width).coerceIn(0f, 1f),
        y = ((untransformed.y - frame.top) / frame.height).coerceIn(0f, 1f)
    )

    return mapData.regions.asReversed().firstOrNull { it.contains(normalized) }
}

private fun mapToCanvas(point: Offset, frame: Rect, scale: Float, pan: Offset): Offset {
    val base = Offset(
        x = frame.left + point.x * frame.width,
        y = frame.top + point.y * frame.height
    )
    val center = frameCenter(frame)
    return Offset(
        x = ((base.x - center.x) * scale) + center.x + pan.x,
        y = ((base.y - center.y) * scale) + center.y + pan.y
    )
}

private fun Offset.coerceForMap(size: Size, scale: Float): Offset {
    if (size.width <= 0f || size.height <= 0f || scale <= 1.01f) return Offset.Zero
    val maxX = size.width * (scale - 1f) * 0.42f
    val maxY = size.height * (scale - 1f) * 0.42f
    return Offset(x.coerceIn(-maxX, maxX), y.coerceIn(-maxY, maxY))
}

private fun frameCenter(frame: Rect): Offset =
    Offset(frame.left + frame.width / 2f, frame.top + frame.height / 2f)

private fun frameContains(frame: Rect, point: Offset): Boolean =
    point.x in frame.left..frame.right && point.y in frame.top..frame.bottom

private fun PampangaMapRegion.toPath(frame: Rect): Path {
    return Path().apply {
        fillType = PathFillType.EvenOdd
        rings.forEach { ring ->
            ring.forEachIndexed { index, point ->
                val x = frame.left + point.x * frame.width
                val y = frame.top + point.y * frame.height
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
    }
}

private fun PampangaMapRegion.contains(point: Offset): Boolean {
    var inside = false
    rings.forEach { ring ->
        if (ringContainsPoint(ring, point)) inside = !inside
    }
    return inside
}

private fun ringContainsPoint(ring: List<Offset>, point: Offset): Boolean {
    if (ring.size < 3) return false
    var inside = false
    var previous = ring.last()

    ring.forEach { current ->
        val crosses = (current.y > point.y) != (previous.y > point.y)
        if (crosses) {
            val denominator = (previous.y - current.y).takeIf { abs(it) > 0.000001f } ?: 0.000001f
            val xIntersection = (previous.x - current.x) * (point.y - current.y) / denominator + current.x
            if (point.x < xIntersection) inside = !inside
        }
        previous = current
    }

    return inside
}

@Composable
private fun rememberPampangaMapData(municipalities: List<Municipality>): PampangaMapData? {
    val context = LocalContext.current
    return remember(context, municipalities) {
        runCatching { loadPampangaMapData(context, municipalities) }.getOrNull()
    }
}

private fun loadPampangaMapData(context: Context, municipalities: List<Municipality>): PampangaMapData {
    val municipalitiesById = municipalities.associateBy { it.id }
    val root = context.assets.open(PAMPANGA_GEOJSON_ASSET).use { stream ->
        InputStreamReader(stream).use { reader ->
            JsonParser.parseReader(reader).asJsonObject
        }
    }

    val rawRegions = root.getAsJsonArray("features").mapNotNull { featureElement ->
        val feature = featureElement.asJsonObject
        val geoName = feature.getAsJsonObject("properties")
            .get("MUNICIPALI")
            .asString
            .trim()
            .uppercase(Locale.ROOT)
        val municipalityId = PAMPANGA_GEO_NAME_TO_ID[geoName] ?: return@mapNotNull null
        val municipality = municipalitiesById[municipalityId] ?: return@mapNotNull null
        val rings = readGeometryRings(feature.getAsJsonObject("geometry"))
        if (rings.isEmpty()) return@mapNotNull null
        RawPampangaRegion(municipality = municipality, rings = rings)
    }

    val allPoints = rawRegions.flatMap { region -> region.rings.flatten() }
    val minLon = allPoints.minOf { it.lon }
    val maxLon = allPoints.maxOf { it.lon }
    val minLat = allPoints.minOf { it.lat }
    val maxLat = allPoints.maxOf { it.lat }
    val middleLat = (minLat + maxLat) / 2.0
    val lonScale = cos(middleLat * PI / 180.0)
    val projectedMinX = minLon * lonScale
    val projectedMaxX = maxLon * lonScale
    val projectedWidth = projectedMaxX - projectedMinX
    val projectedHeight = maxLat - minLat

    val normalizedRegions = rawRegions.map { region ->
        val rings = region.rings.map { ring ->
            ring.map { point ->
                Offset(
                    x = (((point.lon * lonScale) - projectedMinX) / projectedWidth).toFloat(),
                    y = ((maxLat - point.lat) / projectedHeight).toFloat()
                )
            }
        }
        PampangaMapRegion(
            id = region.municipality.id,
            municipality = region.municipality,
            rings = rings,
            label = bestLabelPoint(rings)
        )
    }

    return PampangaMapData(
        regions = normalizedRegions.sortedBy { it.municipality.name },
        aspectRatio = (projectedWidth / projectedHeight).toFloat().coerceIn(0.8f, 1.5f)
    )
}

private fun readGeometryRings(geometry: JsonObject): List<List<GeoPoint>> {
    val coordinates = geometry.getAsJsonArray("coordinates")
    return when (geometry.get("type").asString) {
        "Polygon" -> readPolygonRings(coordinates)
        "MultiPolygon" -> coordinates.flatMap { polygonElement ->
            readPolygonRings(polygonElement.asJsonArray)
        }
        else -> emptyList()
    }
}

private fun readPolygonRings(polygon: JsonArray): List<List<GeoPoint>> {
    return polygon.map { ringElement ->
        ringElement.asJsonArray.map { coordinateElement ->
            val coordinate = coordinateElement.asJsonArray
            GeoPoint(
                lon = coordinate.get(0).asDouble,
                lat = coordinate.get(1).asDouble
            )
        }
    }
}

private fun bestLabelPoint(rings: List<List<Offset>>): Offset {
    val largestRing = rings.maxByOrNull { abs(signedRingArea(it)) } ?: return Offset(0.5f, 0.5f)
    val centroid = ringCentroid(largestRing)
    return if (ringContainsPoint(largestRing, centroid)) {
        centroid
    } else {
        Offset(
            x = largestRing.map { it.x }.average().toFloat(),
            y = largestRing.map { it.y }.average().toFloat()
        )
    }
}

private fun signedRingArea(ring: List<Offset>): Float {
    if (ring.size < 3) return 0f
    var area = 0f
    var previous = ring.last()
    ring.forEach { current ->
        area += previous.x * current.y - current.x * previous.y
        previous = current
    }
    return area / 2f
}

private fun ringCentroid(ring: List<Offset>): Offset {
    val area = signedRingArea(ring)
    if (abs(area) < 0.000001f) {
        return Offset(
            x = ring.map { it.x }.average().toFloat(),
            y = ring.map { it.y }.average().toFloat()
        )
    }

    var cx = 0f
    var cy = 0f
    var previous = ring.last()
    ring.forEach { current ->
        val cross = previous.x * current.y - current.x * previous.y
        cx += (previous.x + current.x) * cross
        cy += (previous.y + current.y) * cross
        previous = current
    }

    val factor = 1f / (6f * area)
    return Offset(cx * factor, cy * factor)
}

private data class GeoPoint(
    val lon: Double,
    val lat: Double
)

private data class RawPampangaRegion(
    val municipality: Municipality,
    val rings: List<List<GeoPoint>>
)

private data class PampangaMapData(
    val regions: List<PampangaMapRegion>,
    val aspectRatio: Float
)

private data class PampangaMapRegion(
    val id: String,
    val municipality: Municipality,
    val rings: List<List<Offset>>,
    val label: Offset
)

private data class MapLabelOffset(
    val xDp: Int = 0,
    val yDp: Int = 8,
    val widthDp: Int = 70
)

private const val PAMPANGA_GEOJSON_ASSET = "geo/pampanga_municities.geojson"

private val PAMPANGA_LABEL_OFFSETS = mapOf(
    "angeles_city" to MapLabelOffset(xDp = -16, yDp = -18, widthDp = 74),
    "apalit" to MapLabelOffset(xDp = 22, yDp = -8, widthDp = 58),
    "arayat" to MapLabelOffset(xDp = 18, yDp = 4, widthDp = 60),
    "bacolor" to MapLabelOffset(xDp = -18, yDp = 10, widthDp = 60),
    "candaba" to MapLabelOffset(xDp = 8, yDp = 8, widthDp = 64),
    "floridablanca" to MapLabelOffset(xDp = 8, yDp = -2, widthDp = 86),
    "guagua" to MapLabelOffset(xDp = -30, yDp = 8, widthDp = 58),
    "lubao" to MapLabelOffset(xDp = -18, yDp = 10, widthDp = 58),
    "mabalacat" to MapLabelOffset(xDp = 18, yDp = -14, widthDp = 76),
    "macabebe" to MapLabelOffset(xDp = 12, yDp = 4, widthDp = 68),
    "magalang" to MapLabelOffset(xDp = 18, yDp = -2, widthDp = 68),
    "masantol" to MapLabelOffset(xDp = 8, yDp = 14, widthDp = 66),
    "mexico" to MapLabelOffset(xDp = -18, yDp = 4, widthDp = 58),
    "minalin" to MapLabelOffset(xDp = 6, yDp = -18, widthDp = 60),
    "porac" to MapLabelOffset(xDp = -10, yDp = 4, widthDp = 58),
    "san_fernando" to MapLabelOffset(xDp = 24, yDp = 10, widthDp = 84),
    "san_luis" to MapLabelOffset(xDp = 24, yDp = 8, widthDp = 62),
    "san_simon" to MapLabelOffset(xDp = 26, yDp = -14, widthDp = 66),
    "santa_ana" to MapLabelOffset(xDp = 16, yDp = 12, widthDp = 68),
    "santa_rita" to MapLabelOffset(xDp = -18, yDp = -18, widthDp = 72),
    "santo_tomas" to MapLabelOffset(xDp = 32, yDp = 0, widthDp = 78),
    "sasmuan" to MapLabelOffset(xDp = -4, yDp = 12, widthDp = 62)
)

private val PAMPANGA_GEO_NAME_TO_ID = mapOf(
    "ANGELES CITY" to "angeles_city",
    "APALIT" to "apalit",
    "ARAYAT" to "arayat",
    "BACOLOR" to "bacolor",
    "CANDABA" to "candaba",
    "CITY OF SAN FERNANDO" to "san_fernando",
    "FLORIDABLANCA" to "floridablanca",
    "GUAGUA" to "guagua",
    "LUBAO" to "lubao",
    "MABALACAT CITY" to "mabalacat",
    "MACABEBE" to "macabebe",
    "MAGALANG" to "magalang",
    "MASANTOL" to "masantol",
    "MEXICO" to "mexico",
    "MINALIN" to "minalin",
    "PORAC" to "porac",
    "SAN FERNANDO" to "san_fernando",
    "SAN LUIS" to "san_luis",
    "SAN SIMON" to "san_simon",
    "SANTA ANA" to "santa_ana",
    "SANTA RITA" to "santa_rita",
    "SANTO TOMAS" to "santo_tomas",
    "STO. TOMAS" to "santo_tomas",
    "SASMUAN" to "sasmuan"
)
