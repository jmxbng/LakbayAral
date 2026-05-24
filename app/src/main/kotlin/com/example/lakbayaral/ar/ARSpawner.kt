package com.example.lakbayaral.ar

import android.content.Context
import android.graphics.Color as AndroidColor
import android.util.Log
import com.example.lakbayaral.data.models.HistoricalObject
import com.example.lakbayaral.data.models.ObjectType
import com.google.ar.core.HitResult
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Scale
import io.github.sceneview.math.Size
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.CylinderNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.node.SphereNode
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sin

private const val TAG = "ARSpawner"
private const val PLACE_MODEL_HEIGHT_METERS = 0.78f
private const val PERSON_MODEL_HEIGHT_METERS = 0.58f
private const val CONTENT_NODE_PREFIX = "content:"
private const val FLOOR_SPAWN_SCALE = 0.82f
private const val FLOOR_HOLD_NANOS = 240_000_000L
private const val CINEMATIC_LIFT_NANOS = 1_650_000_000L
private const val DEFAULT_EYE_HEIGHT_METERS = 1.35f
private const val MIN_LIFT_HEIGHT_METERS = 0.92f
private const val MAX_LIFT_HEIGHT_METERS = 1.38f
private const val ROTATION_DEGREES_PER_PIXEL = 0.16f
private const val ROTATION_MAX_EVENT_PIXELS = 42f
private const val ROTATION_INPUT_BLEND = 0.82f
private const val ROTATION_FOLLOW_SPEED = 34f
private const val ROTATION_SNAP_DEGREES = 0.02f
private const val ROTATION_YAW_COMPACT_THRESHOLD = 7200f
private const val NANOS_TO_SECONDS = 1_000_000_000f

class ARSpawner(
    private val context: Context,
    private val sceneView: ARSceneView,
    private val onObjectTapped: (HistoricalObject) -> Unit
) {

    private val spawnedNodes = mutableListOf<Node>()
    private val objectMotions = mutableMapOf<Node, ObjectMotion>()

    fun spawnObject(historicalObject: HistoricalObject, hitResult: HitResult): Boolean {
        val anchor = hitResult.createAnchorOrNull() ?: run {
            Log.w(TAG, "Could not create anchor for ${historicalObject.name}")
            return false
        }

        val anchorNode = AnchorNode(
            engine = sceneView.engine,
            anchor = anchor,
            onTrackingStateChanged = { state ->
                Log.d(TAG, "Anchor ${historicalObject.id} tracking state: $state")
            }
        ).apply {
            name = "anchor:${historicalObject.id}"
            isTouchable = false
            isHittable = false
            lockTransformEditing()
        }

        val contentRoot = Node(sceneView.engine).apply {
            name = "$CONTENT_NODE_PREFIX${historicalObject.id}"
            position = Position(0f, 0f, 0f)
            rotation = Rotation(0f, 0f, 0f)
            scale = Scale(FLOOR_SPAWN_SCALE)
            bindTap(historicalObject)
        }
        objectMotions[contentRoot] = ObjectMotion()
        val liftHeightMeters = calculateEyeLevelLift(historicalObject, hitResult)

        anchorNode.addChildNode(contentRoot)

        sceneView.addChildNode(anchorNode)
        spawnedNodes.add(anchorNode)

        val modelFile = historicalObject.modelAsset
        if (modelFile.isNotBlank() && assetExists(modelFile)) {
            sceneView.modelLoader.loadModelInstanceAsync(modelFile) { modelInstance ->
                if (anchorNode !in spawnedNodes) return@loadModelInstanceAsync

                if (modelInstance != null) {
                    contentRoot.addChildNode(createModelNode(historicalObject, modelInstance))
                    startCinematicLift(contentRoot, liftHeightMeters)
                    Log.d(TAG, "Loaded model for ${historicalObject.name}: $modelFile")
                } else {
                    Log.w(TAG, "Model load failed for $modelFile; using fallback marker")
                    contentRoot.addChildNode(createFallbackMarker(historicalObject))
                    startCinematicLift(contentRoot, liftHeightMeters)
                }
            }
        } else {
            Log.w(TAG, "Missing model asset '$modelFile'; using fallback marker")
            contentRoot.addChildNode(createFallbackMarker(historicalObject))
            startCinematicLift(contentRoot, liftHeightMeters)
        }

        Log.d(TAG, "Spawned ${historicalObject.name}")
        return true
    }

    fun findObjectRoot(node: Node?): Node? {
        var current = node
        while (current != null) {
            if (current.name?.startsWith(CONTENT_NODE_PREFIX) == true) return current
            current = current.parent
        }
        return null
    }

    fun rotateObjectFromDrag(node: Node?, deltaPixels: Float): Boolean {
        val root = findObjectRoot(node) ?: return false
        val motion = objectMotions.getOrPut(root) {
            ObjectMotion(displayedYaw = root.rotation.y, targetYaw = root.rotation.y)
        }
        val stableDeltaPixels = deltaPixels.coerceIn(-ROTATION_MAX_EVENT_PIXELS, ROTATION_MAX_EVENT_PIXELS)
        val smoothedDeltaPixels = if (motion.hasDragSample) {
            motion.smoothedDragPixels +=
                (stableDeltaPixels - motion.smoothedDragPixels) * ROTATION_INPUT_BLEND
            motion.smoothedDragPixels
        } else {
            motion.hasDragSample = true
            stableDeltaPixels
        }
        motion.targetYaw -= smoothedDeltaPixels * ROTATION_DEGREES_PER_PIXEL
        return true
    }

    fun beginObjectRotation(node: Node?): Boolean {
        val root = findObjectRoot(node) ?: return false
        val motion = objectMotions.getOrPut(root) {
            ObjectMotion(displayedYaw = root.rotation.y, targetYaw = root.rotation.y)
        }
        motion.targetYaw = motion.displayedYaw
        motion.hasDragSample = false
        motion.smoothedDragPixels = 0f
        return true
    }

    fun endObjectRotation(node: Node?) {
        val root = findObjectRoot(node) ?: return
        objectMotions[root]?.apply {
            hasDragSample = false
            smoothedDragPixels = 0f
        }
    }

    fun clearAll() {
        spawnedNodes.forEach { node ->
            sceneView.removeChildNode(node)
            node.destroy()
        }
        spawnedNodes.clear()
        objectMotions.clear()
    }

    fun removeLast() {
        if (spawnedNodes.isNotEmpty()) {
            val node = spawnedNodes.removeAt(spawnedNodes.lastIndex)
            removeMotionFor(node)
            sceneView.removeChildNode(node)
            node.destroy()
        }
    }

    private data class ObjectMotion(
        var displayedYaw: Float = 0f,
        var targetYaw: Float = 0f,
        var smoothedDragPixels: Float = 0f,
        var hasDragSample: Boolean = false,
        var lastFrameNanos: Long? = null,
        var liftStartNanos: Long? = null,
        var liftComplete: Boolean = false
    )

    private fun calculateEyeLevelLift(
        historicalObject: HistoricalObject,
        hitResult: HitResult
    ): Float {
        val floorY = hitResult.hitPose.ty()
        val cameraHeight = sceneView.frame?.camera?.pose?.ty()?.minus(floorY)
            ?: DEFAULT_EYE_HEIGHT_METERS
        val objectCenterOffset = when (historicalObject.type) {
            ObjectType.PLACE -> PLACE_MODEL_HEIGHT_METERS * 0.48f
            ObjectType.PERSON -> PERSON_MODEL_HEIGHT_METERS * 0.50f
        }
        return (cameraHeight - objectCenterOffset)
            .coerceIn(MIN_LIFT_HEIGHT_METERS, MAX_LIFT_HEIGHT_METERS)
    }

    private fun startCinematicLift(contentRoot: Node, targetHeightMeters: Float) {
        val motion = objectMotions.getOrPut(contentRoot) {
            ObjectMotion(displayedYaw = contentRoot.rotation.y, targetYaw = contentRoot.rotation.y)
        }
        contentRoot.onFrame = { frameTimeNanos ->
            val deltaSeconds = motion.lastFrameNanos?.let { previousFrame ->
                ((frameTimeNanos - previousFrame).toFloat() / NANOS_TO_SECONDS).coerceIn(0f, 0.05f)
            } ?: (1f / 60f)
            motion.lastFrameNanos = frameTimeNanos

            updateLift(contentRoot, targetHeightMeters, frameTimeNanos, motion)
            updateRotation(contentRoot, deltaSeconds, motion)
        }
    }

    private fun updateLift(
        contentRoot: Node,
        targetHeightMeters: Float,
        frameTimeNanos: Long,
        motion: ObjectMotion
    ) {
        if (motion.liftComplete) return

        val startedAt = motion.liftStartNanos ?: frameTimeNanos.also {
            motion.liftStartNanos = it
        }
        val elapsed = frameTimeNanos - startedAt

        if (elapsed < FLOOR_HOLD_NANOS) {
            contentRoot.position = Position(0f, 0f, 0f)
            contentRoot.scale = Scale(FLOOR_SPAWN_SCALE)
            return
        }

        val progress = ((elapsed - FLOOR_HOLD_NANOS).toFloat() / CINEMATIC_LIFT_NANOS)
            .coerceIn(0f, 1f)
        val eased = easeOutCubic(progress)
        val settleArc = sin(progress * Math.PI.toFloat()) * 0.026f
        val scalePulse = sin(progress * Math.PI.toFloat()) * 0.035f
        val scale = FLOOR_SPAWN_SCALE + (1f - FLOOR_SPAWN_SCALE) * eased + scalePulse

        contentRoot.position = Position(0f, targetHeightMeters * eased + settleArc, 0f)
        contentRoot.scale = Scale(scale)

        if (progress >= 1f) {
            contentRoot.position = Position(0f, targetHeightMeters, 0f)
            contentRoot.scale = Scale(1f)
            motion.liftComplete = true
        }
    }

    private fun updateRotation(contentRoot: Node, deltaSeconds: Float, motion: ObjectMotion) {
        val remainingYaw = motion.targetYaw - motion.displayedYaw
        if (abs(remainingYaw) <= ROTATION_SNAP_DEGREES) {
            motion.displayedYaw = motion.targetYaw
        } else {
            val smoothing = 1f - exp(-ROTATION_FOLLOW_SPEED * deltaSeconds)
            motion.displayedYaw += remainingYaw * smoothing
        }
        compactYawIfNeeded(motion)
        contentRoot.rotation = Rotation(0f, motion.displayedYaw, 0f)
    }

    private fun Node.lockTransformEditing() {
        isEditable = false
        isPositionEditable = false
        isRotationEditable = false
        isScaleEditable = false
    }

    private fun easeOutCubic(progress: Float): Float {
        val inverse = 1f - progress
        return 1f - inverse * inverse * inverse
    }

    private fun compactYawIfNeeded(motion: ObjectMotion) {
        if (abs(motion.displayedYaw) < ROTATION_YAW_COMPACT_THRESHOLD) return

        val yawOffset = (motion.displayedYaw / 360f).toInt() * 360f
        motion.displayedYaw -= yawOffset
        motion.targetYaw -= yawOffset
    }

    private fun removeMotionFor(node: Node) {
        objectMotions.remove(node)
        node.childNodes.forEach(::removeMotionFor)
    }

    // Optional floor spawn effect kept detached so only the intended object is rendered by default.
    private fun createPlacementPedestal(historicalObject: HistoricalObject): Node {
        val engine = sceneView.engine
        val gold = sceneView.materialLoader.createColorInstance(
            AndroidColor.argb(245, 200, 162, 74),
            metallic = 0.2f,
            roughness = 0.22f,
            reflectance = 0.75f
        )
        val neon = sceneView.materialLoader.createColorInstance(
            AndroidColor.argb(230, 77, 227, 255),
            metallic = 0.0f,
            roughness = 0.16f,
            reflectance = 0.95f
        )
        val darkGlass = sceneView.materialLoader.createColorInstance(
            AndroidColor.argb(225, 17, 24, 39),
            metallic = 0.05f,
            roughness = 0.3f,
            reflectance = 0.7f
        )

        val pedestal = Node(engine).apply {
            name = "pedestal:${historicalObject.id}"
            bindTap(historicalObject)
        }

        val base = CylinderNode(
            engine = engine,
            radius = 0.28f,
            height = 0.028f,
            center = Position(0f, 0.014f, 0f),
            sideCount = 72,
            materialInstance = darkGlass
        )

        val outerRing = CylinderNode(
            engine = engine,
            radius = 0.32f,
            height = 0.006f,
            center = Position(0f, 0.034f, 0f),
            sideCount = 96,
            materialInstance = neon
        )

        val innerRing = CylinderNode(
            engine = engine,
            radius = 0.18f,
            height = 0.008f,
            center = Position(0f, 0.044f, 0f),
            sideCount = 72,
            materialInstance = gold
        )

        val beacon = CylinderNode(
            engine = engine,
            radius = 0.012f,
            height = 0.46f,
            center = Position(0f, 0.27f, 0f),
            sideCount = 24,
            materialInstance = neon
        )

        listOf(base, outerRing, innerRing, beacon).forEach { node ->
            node.bindTap(historicalObject)
            pedestal.addChildNode(node)
        }

        pedestal.onFrame = { frameTimeNanos ->
            val seconds = frameTimeNanos / 1_000_000_000f
            val pulse = 1f + sin(seconds * 2.8f) * 0.045f
            outerRing.scale = Scale(pulse, 1f, pulse)
            beacon.scale = Scale(1f, 0.75f + sin(seconds * 3.4f) * 0.18f, 1f)
        }

        return pedestal
    }

    private fun createModelNode(
        historicalObject: HistoricalObject,
        modelInstance: io.github.sceneview.model.ModelInstance
    ): ModelNode {
        return ModelNode(
            modelInstance = modelInstance,
            autoAnimate = false,
            scaleToUnits = if (historicalObject.type == ObjectType.PLACE) {
                PLACE_MODEL_HEIGHT_METERS
            } else {
                PERSON_MODEL_HEIGHT_METERS
            },
            centerOrigin = Position(0f, -1f, 0f)
        ).apply {
            name = "model:${historicalObject.id}"
            position = Position(0f, 0.045f, 0f)
            isShadowCaster = false
            isShadowReceiver = false
            setScreenSpaceContactShadows(false)
            setCulling(false)
            bindTap(historicalObject)
        }
    }

    private fun createFallbackMarker(historicalObject: HistoricalObject): Node {
        val engine = sceneView.engine
        val gold = sceneView.materialLoader.createColorInstance(
            AndroidColor.argb(245, 200, 162, 74),
            metallic = 0.15f,
            roughness = 0.25f,
            reflectance = 0.7f
        )
        val neon = sceneView.materialLoader.createColorInstance(
            AndroidColor.argb(230, 77, 227, 255),
            metallic = 0.0f,
            roughness = 0.18f,
            reflectance = 0.9f
        )
        val darkGlass = sceneView.materialLoader.createColorInstance(
            AndroidColor.argb(225, 17, 24, 39),
            metallic = 0.05f,
            roughness = 0.35f,
            reflectance = 0.6f
        )

        val marker = Node(engine).apply {
            name = "fallback:${historicalObject.id}"
            bindTap(historicalObject)
        }

        val pieces = when (historicalObject.type) {
            ObjectType.PLACE -> createPlaceMarkerPieces(historicalObject, gold, neon, darkGlass)
            ObjectType.PERSON -> createPersonMarkerPieces(gold, neon, darkGlass)
        }

        pieces.forEach { node ->
            node.bindTap(historicalObject)
            marker.addChildNode(node)
        }

        marker.onFrame = { frameTimeNanos ->
            val seconds = frameTimeNanos / 1_000_000_000f
            marker.position = Position(0f, 0.015f + sin(seconds * 2.1f) * 0.012f, 0f)
            marker.rotation = Rotation(0f, (seconds * 18f) % 360f, 0f)
        }

        return marker
    }

    private fun createPlaceMarkerPieces(
        historicalObject: HistoricalObject,
        gold: com.google.android.filament.MaterialInstance,
        neon: com.google.android.filament.MaterialInstance,
        darkGlass: com.google.android.filament.MaterialInstance
    ): List<Node> {
        val engine = sceneView.engine
        return when {
            historicalObject.id.contains("bridge") -> listOf(
                CubeNode(engine, Size(0.42f, 0.045f, 0.08f), Position(0f, 0.16f, 0f), gold),
                CubeNode(engine, Size(0.055f, 0.18f, 0.055f), Position(-0.13f, 0.095f, 0f), neon),
                CubeNode(engine, Size(0.055f, 0.18f, 0.055f), Position(0.13f, 0.095f, 0f), neon)
            )

            historicalObject.id.contains("church") -> listOf(
                CubeNode(engine, Size(0.22f, 0.18f, 0.14f), Position(0f, 0.15f, 0f), gold),
                CubeNode(engine, Size(0.07f, 0.28f, 0.08f), Position(-0.105f, 0.19f, 0f), darkGlass),
                CubeNode(engine, Size(0.07f, 0.28f, 0.08f), Position(0.105f, 0.19f, 0f), darkGlass),
                SphereNode(engine, radius = 0.045f, center = Position(0f, 0.29f, 0f), materialInstance = neon)
            )

            else -> listOf(
                CylinderNode(engine, radius = 0.075f, height = 0.25f, center = Position(0f, 0.17f, 0f), sideCount = 32, materialInstance = gold),
                SphereNode(engine, radius = 0.075f, center = Position(0f, 0.33f, 0f), materialInstance = neon)
            )
        }
    }

    private fun createPersonMarkerPieces(
        gold: com.google.android.filament.MaterialInstance,
        neon: com.google.android.filament.MaterialInstance,
        darkGlass: com.google.android.filament.MaterialInstance
    ): List<Node> {
        val engine = sceneView.engine
        return listOf(
            CubeNode(engine, Size(0.16f, 0.12f, 0.11f), Position(0f, 0.13f, 0f), gold),
            SphereNode(engine, radius = 0.075f, center = Position(0f, 0.25f, 0f), materialInstance = gold),
            CylinderNode(engine, radius = 0.105f, height = 0.025f, center = Position(0f, 0.315f, 0f), sideCount = 40, materialInstance = neon),
            CubeNode(engine, Size(0.24f, 0.035f, 0.08f), Position(0f, 0.07f, 0f), darkGlass)
        )
    }

    private fun Node.bindTap(historicalObject: HistoricalObject) {
        isTouchable = true
        isHittable = true
        lockTransformEditing()
        onSingleTapConfirmed = {
            onObjectTapped(historicalObject)
            true
        }
    }

    private fun assetExists(assetPath: String): Boolean {
        return runCatching {
            context.assets.open(assetPath).use { true }
        }.getOrDefault(false)
    }
}
