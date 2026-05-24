package com.example.lakbayaral.ar

import android.content.Context
import android.util.Log
import com.google.ar.core.*
import com.google.ar.core.exceptions.*

private const val TAG = "ARController"

/**
 * Manages the ARCore Session lifecycle.
 * SceneView handles most lifecycle automatically; this class provides
 * utilities for plane detection checks and session configuration.
 */
object ARController {

    /** Check if ARCore is installed and supported on this device. */
    fun checkARCoreAvailability(context: Context): ARAvailability {
        return try {
            when (ArCoreApk.getInstance().checkAvailability(context)) {
                ArCoreApk.Availability.SUPPORTED_INSTALLED -> ARAvailability.Available
                ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED,
                ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD -> ARAvailability.NeedsInstall
                else -> ARAvailability.Unsupported
            }
        } catch (e: Exception) {
            Log.e(TAG, "ARCore availability check failed", e)
            ARAvailability.Unsupported
        }
    }

    /** Configure ARCore session with horizontal plane detection. */
    fun configureSession(session: Session) {
        val config = Config(session).apply {
            planeFindingMode    = Config.PlaneFindingMode.HORIZONTAL
            updateMode          = Config.UpdateMode.LATEST_CAMERA_IMAGE
            lightEstimationMode = Config.LightEstimationMode.DISABLED
            cloudAnchorMode     = Config.CloudAnchorMode.DISABLED
        }
        session.configure(config)
    }
}

enum class ARAvailability { Available, NeedsInstall, Unsupported }
