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
import kotlin.math.sin

private const val TAG = "ARSpawner"
private const val PLACE_MODEL_HEIGHT_METERS = 0.78f
private const val PERSON_MODEL_HEIGHT_METERS = 0.58f

class ARSpawner(
    private val context: Context,
    private val sceneView: ARSceneView,
    private val onObjectTapped: (HistoricalObject) -> Unit
) {

    private val spawnedNodes = mutableListOf<Node>()

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
        }

        val contentRoot = Node(sceneView.engine).apply {
            name = "content:${historicalObject.id}"
            bindTap(historicalObject)
        }
        /*contentRoot.addChildNode(createPlacementPedestal(historicalObject))*/
        anchorNode.addChildNode(contentRoot)

        sceneView.addChildNode(anchorNode)
        spawnedNodes.add(anchorNode)

        val modelFile = historicalObject.modelAsset
        if (modelFile.isNotBlank() && assetExists(modelFile)) {
            sceneView.modelLoader.loadModelInstanceAsync(modelFile) { modelInstance ->
                if (anchorNode !in spawnedNodes) return@loadModelInstanceAsync

                if (modelInstance != null) {
                    contentRoot.addChildNode(createModelNode(historicalObject, modelInstance))
                    Log.d(TAG, "Loaded model for ${historicalObject.name}: $modelFile")
                } else {
                    Log.w(TAG, "Model load failed for $modelFile; using fallback marker")
                    contentRoot.addChildNode(createFallbackMarker(historicalObject))
                }
            }
        } else {
            Log.w(TAG, "Missing model asset '$modelFile'; using fallback marker")
            contentRoot.addChildNode(createFallbackMarker(historicalObject))
        }

        Log.d(TAG, "Spawned ${historicalObject.name}")
        return true
    }

    fun clearAll() {
        spawnedNodes.forEach { node ->
            sceneView.removeChildNode(node)
            node.destroy()
        }
        spawnedNodes.clear()
    }

    fun removeLast() {
        if (spawnedNodes.isNotEmpty()) {
            val node = spawnedNodes.removeAt(spawnedNodes.lastIndex)
            sceneView.removeChildNode(node)
            node.destroy()
        }
    }

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
            isShadowCaster = true
            isShadowReceiver = true
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
