package com.example.lakbayaral.ar

import android.content.Context
import android.util.Log
import com.example.lakbayaral.data.models.HistoricalObject
import com.example.lakbayaral.data.models.ObjectType
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Scale
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node

private const val TAG = "ARSpawner"

/**
 * Handles spawning and removing HistoricalObject nodes in the ARSceneView.
 *
 * Object placement flow:
 *  1. User taps a detected plane.
 *  2. ARSpawner.spawnObject() is called with the HistoricalObject and hit pose.
 *  3. An AnchorNode is created from the hit result, and a ModelNode is added as its child.
 *  4. A tap listener is attached — when the node is tapped, onObjectTapped is invoked
 *     so the UI can show the history panel.
 */
class ARSpawner(
    private val context: Context,
    private val sceneView: ARSceneView,
    private val onObjectTapped: (HistoricalObject) -> Unit
) {

    private val spawnedNodes = mutableListOf<Node>()

    /**
     * Spawn a 3D glTF model for the given [historicalObject] at the tapped [hitResult].
     */
    fun spawnObject(historicalObject: HistoricalObject, hitResult: com.google.ar.core.HitResult) {
        val modelFile = historicalObject.modelAsset

        // Create an AnchorNode to stay fixed in the real world
        val anchorNode = AnchorNode(sceneView.engine, hitResult.createAnchor())

        // Load the model asynchronously
        if (modelFile.isNotEmpty()) {
            sceneView.modelLoader.loadModelInstanceAsync(modelFile) { modelInstance ->
                if (modelInstance != null) {
                    val modelNode = ModelNode(
                        modelInstance = modelInstance,
                        autoAnimate     = true,
                        scaleToUnits    = 0.6f,
                        centerOrigin    = Position(0f, 0f, 0f)
                    ).apply {
                        scale = when (historicalObject.type) {
                            ObjectType.PLACE  -> Scale(0.5f)
                            ObjectType.PERSON -> Scale(0.3f)
                        }
                        // In SceneView 2.2.1, we use onSingleTapConfirmed
                        onSingleTapConfirmed = { _ ->
                            onObjectTapped(historicalObject)
                            true
                        }
                    }
                    anchorNode.addChildNode(modelNode)
                } else {
                    Log.w(TAG, "Model load failed for $modelFile")
                }
            }
        }

        sceneView.addChildNode(anchorNode)
        spawnedNodes.add(anchorNode)
        Log.d(TAG, "Spawned: ${historicalObject.name}")
    }

    /** Remove all spawned nodes from the scene. */
    fun clearAll() {
        spawnedNodes.forEach { node ->
            sceneView.removeChildNode(node)
            node.destroy()
        }
        spawnedNodes.clear()
    }

    /** Remove the most recently spawned node (undo last spawn). */
    fun removeLast() {
        if (spawnedNodes.isNotEmpty()) {
            val node = spawnedNodes.removeAt(spawnedNodes.lastIndex)
            sceneView.removeChildNode(node)
            node.destroy()
        }
    }
}
