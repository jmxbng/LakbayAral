package com.example.lakbayaral.ar

import com.example.lakbayaral.data.models.HistoricalObject
import com.google.ar.core.HitResult
import com.google.ar.core.Plane

/**
 * Coordinates tap events between the ARSceneView and the composable UI layer.
 *
 * The ARModeScreen creates one ARInteractionManager and passes callbacks:
 *  - onPlacementTap: called when user taps on a detected plane (spawn next object)
 *  - onObjectTap: called when user taps a previously-spawned model (show panel)
 */
class ARInteractionManager(
    private val objectList       : List<HistoricalObject>,
    private val onObjectTap      : (HistoricalObject) -> Unit,
    private val onPlacementTap   : (HistoricalObject, HitResult) -> Unit,
    private val onOutOfObjects   : () -> Unit
) {
    private var currentIndex = 0

    /** True when all objects in the list have been placed. */
    val allPlaced: Boolean get() = currentIndex >= objectList.size

    /** Returns the object that should be placed next, or null. */
    val nextObject: HistoricalObject? get() = objectList.getOrNull(currentIndex)

    /**
     * Handle a tap hit result on a real-world plane.
     * Places the next object in the sequence.
     */
    fun onPlaneTap(hitResult: HitResult, plane: Plane) {
        val obj = nextObject ?: run {
            onOutOfObjects()
            return
        }
        onPlacementTap(obj, hitResult)
        currentIndex++
        if (currentIndex >= objectList.size) onOutOfObjects()
    }

    fun reset() { currentIndex = 0 }
}
