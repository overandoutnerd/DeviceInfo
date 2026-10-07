package com.overandoutnerd.deviceinfo.home.camera

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log
import com.overandoutnerd.deviceinfo.home.UserDeviceDetailsProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CameraDetailsRepository(private val context: Context) {
    suspend fun getCameraDetails(): List<List<UserDeviceDetailsProperty>> {
        return withContext(Dispatchers.IO) {
            val cameraDetails: MutableList<List<UserDeviceDetailsProperty>> = mutableListOf()
            try {
                val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val cameraIds = cameraManager.cameraIdList
                for (id in cameraIds) {
                    val cameraInfo = getCameraInfo(cameraManager, id)
                    cameraDetails.add(cameraInfo)
                }
                cameraDetails
            } catch (e: Exception) {
                Log.e("DeviceComponentsDetailsRepository", "Method: getCameraDetails(), Error: ${e.message}")
                cameraDetails
            }
        }
    }
    private fun getCameraInfo(cameraManager: CameraManager, id: String): List<UserDeviceDetailsProperty> {
        val cameraDetails: MutableList<UserDeviceDetailsProperty> = mutableListOf()
        return try {
            val characteristics = cameraManager.getCameraCharacteristics(id)

            val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
            val cameraTypeValue = if (lensFacing != null) {
                when(lensFacing) {
                    CameraCharacteristics.LENS_FACING_FRONT -> "Front"
                    CameraCharacteristics.LENS_FACING_BACK -> "Back"
                    CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
                    else -> "Unknown"
                }
            } else {
                "Unidentified"
            }
            val cameraType = UserDeviceDetailsProperty("Type", cameraTypeValue)
            cameraDetails.add(cameraType)

            val pixelArraySize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)
            val megaPixelsValue = if (pixelArraySize != null) {
                (pixelArraySize.width * pixelArraySize.height) / 1_000_000.0
            } else { 0.0 }
            val megaPixels = UserDeviceDetailsProperty("Mega Pixels", megaPixelsValue.toString())
            cameraDetails.add(megaPixels)

            val aberrationModesValue = characteristics.get(CameraCharacteristics.COLOR_CORRECTION_AVAILABLE_ABERRATION_MODES)
            aberrationModesValue?.let {
                val aberrationModesNames = it.map { mode ->
                    when (mode) {
                        CameraCharacteristics.COLOR_CORRECTION_ABERRATION_MODE_OFF -> "OFF"
                        CameraCharacteristics.COLOR_CORRECTION_ABERRATION_MODE_FAST -> "Fast"
                        CameraCharacteristics.COLOR_CORRECTION_ABERRATION_MODE_HIGH_QUALITY -> "High Quality"
                        else -> "Unknown"
                    }
                }
                val aberrationModes = UserDeviceDetailsProperty("Aberration Modes", aberrationModesNames.joinToString())
                cameraDetails.add(aberrationModes)
            }

            val antibandingModesValue = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_ANTIBANDING_MODES)
            antibandingModesValue?.let {
                val antibandingModesNames = it.map { mode ->
                    when (mode) {
                        CameraCharacteristics.CONTROL_AE_ANTIBANDING_MODE_OFF -> "Off"
                        CameraCharacteristics.CONTROL_AE_ANTIBANDING_MODE_AUTO -> "Auto"
                        CameraCharacteristics.CONTROL_AE_ANTIBANDING_MODE_50HZ -> "50Hz"
                        CameraCharacteristics.CONTROL_AE_ANTIBANDING_MODE_60HZ -> "60Hz"
                        else -> "Unknown"
                    }
                }
                val antibandingModes = UserDeviceDetailsProperty("Antibanding Modes", antibandingModesNames.joinToString())
                cameraDetails.add(antibandingModes)
            }

            val autoExposureModesValue = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES)
            autoExposureModesValue?.let {
                val autoExposureModesNames = it.map { mode ->
                    when (mode) {
                        CameraCharacteristics.CONTROL_AE_MODE_ON -> "On"
                        CameraCharacteristics.CONTROL_AE_MODE_OFF -> "Off"
                        CameraCharacteristics.CONTROL_AE_MODE_ON_AUTO_FLASH -> "Auto Flash"
                        CameraCharacteristics.CONTROL_AE_MODE_ON_ALWAYS_FLASH -> "Always Flash"
                        CameraCharacteristics.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE -> "Auto Flash Red Eye"
                        CameraCharacteristics.CONTROL_AE_MODE_ON_EXTERNAL_FLASH -> "External Flash"
                        else -> "Unknown"
                    }
                }
                val autoExposureModes = UserDeviceDetailsProperty("Auto Exposure Modes", autoExposureModesNames.joinToString())
                cameraDetails.add(autoExposureModes)
            }

            val autoFocusModesValue = characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)
            autoFocusModesValue?.let {
                val autoFocusModesNames = it.map { mode ->
                    when (mode) {
                        CameraCharacteristics.CONTROL_AF_MODE_OFF -> "Off"
                        CameraCharacteristics.CONTROL_AF_MODE_AUTO -> "Auto"
                        CameraCharacteristics.CONTROL_AF_MODE_EDOF -> "Digital"
                        CameraCharacteristics.CONTROL_AF_MODE_MACRO -> "Macro"
                        CameraCharacteristics.CONTROL_AF_MODE_CONTINUOUS_PICTURE -> "Continuous Picture"
                        CameraCharacteristics.CONTROL_AF_MODE_CONTINUOUS_VIDEO -> "Continuous Video"
                        else -> "Unknown"
                    }
                }
                val autoFocusModes = UserDeviceDetailsProperty("Auto Focus Modes", autoFocusModesNames.joinToString())
                cameraDetails.add(autoFocusModes)
            }

            val effectsValue = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS)
            effectsValue?.let {
                val effectsNames = it.map { effect ->
                    when(effect) {
                        CameraCharacteristics.CONTROL_EFFECT_MODE_OFF -> "Off"
                        CameraCharacteristics.CONTROL_EFFECT_MODE_AQUA -> "Aqua"
                        CameraCharacteristics.CONTROL_EFFECT_MODE_MONO -> "Mono"
                        CameraCharacteristics.CONTROL_EFFECT_MODE_SEPIA -> "Sepia"
                        CameraCharacteristics.CONTROL_EFFECT_MODE_NEGATIVE -> "Negative"
                        CameraCharacteristics.CONTROL_EFFECT_MODE_BLACKBOARD -> "Blackboard"
                        CameraCharacteristics.CONTROL_EFFECT_MODE_WHITEBOARD -> "Whiteboard"
                        CameraCharacteristics.CONTROL_EFFECT_MODE_POSTERIZE -> "Posterization"
                        CameraCharacteristics.CONTROL_EFFECT_MODE_SOLARIZE -> "Solarize"
                        else -> "Unknown"
                    }
                }
                val effects = UserDeviceDetailsProperty("Effects", effectsNames.joinToString())
                cameraDetails.add(effects)
            }

            val sceneModesValue = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES)
            sceneModesValue?.let {
                val sceneModesNames = it.map { sceneMode ->
                    when(sceneMode) {
                        CameraCharacteristics.CONTROL_SCENE_MODE_HDR -> "HDR"
                        CameraCharacteristics.CONTROL_SCENE_MODE_SNOW -> "Snow"
                        CameraCharacteristics.CONTROL_SCENE_MODE_BEACH -> "Beach"
                        CameraCharacteristics.CONTROL_SCENE_MODE_NIGHT -> "Night"
                        CameraCharacteristics.CONTROL_SCENE_MODE_PARTY -> "Party"
                        CameraCharacteristics.CONTROL_SCENE_MODE_ACTION -> "Action"
                        CameraCharacteristics.CONTROL_SCENE_MODE_BARCODE -> "Barcode"
                        CameraCharacteristics.CONTROL_SCENE_MODE_CANDLELIGHT -> "Candle Light"
                        CameraCharacteristics.CONTROL_SCENE_MODE_DISABLED -> "Disabled"
                        CameraCharacteristics.CONTROL_SCENE_MODE_FACE_PRIORITY -> "Face Priority"
                        CameraCharacteristics.CONTROL_SCENE_MODE_FIREWORKS -> "Fireworks"
                        CameraCharacteristics.CONTROL_SCENE_MODE_LANDSCAPE -> "Landscape"
                        CameraCharacteristics.CONTROL_SCENE_MODE_PORTRAIT -> "Portrait"
                        CameraCharacteristics.CONTROL_SCENE_MODE_NIGHT_PORTRAIT -> "Night Portrait"
                        CameraCharacteristics.CONTROL_SCENE_MODE_SPORTS -> "Sports"
                        CameraCharacteristics.CONTROL_SCENE_MODE_STEADYPHOTO -> "Steady Photo"
                        CameraCharacteristics.CONTROL_SCENE_MODE_SUNSET -> "Sunset"
                        CameraCharacteristics.CONTROL_SCENE_MODE_THEATRE -> "Theatre"
                        else -> ""
                    }
                }
                val sceneModes = UserDeviceDetailsProperty("Scene Modes", sceneModesNames.joinToString())
                cameraDetails.add(sceneModes)
            }

            val videoStabilizationModesValue = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES)
            videoStabilizationModesValue?.let {
                val videoStabilizationModesNames = it.map{ videoStabilizationMode ->
                    when(videoStabilizationMode) {
                        CameraCharacteristics.CONTROL_VIDEO_STABILIZATION_MODE_ON -> "On"
                        CameraCharacteristics.CONTROL_VIDEO_STABILIZATION_MODE_OFF -> "Off"
                        CameraCharacteristics.CONTROL_VIDEO_STABILIZATION_MODE_PREVIEW_STABILIZATION -> "Preview Stabilization"
                        else -> "Unknown"
                    }
                }
                val videoStabilizationModes = UserDeviceDetailsProperty("Video Staboilization Modes", videoStabilizationModesNames.joinToString())
                cameraDetails.add(videoStabilizationModes)
            }

            val awbModesValue = characteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES)
            awbModesValue?.let {
                val awbModesNames = it.map{ awbMode ->
                    when(awbMode) {
                        CameraCharacteristics.CONTROL_AWB_MODE_OFF -> "Off"
                        CameraCharacteristics.CONTROL_AWB_MODE_AUTO -> "Auto"
                        CameraCharacteristics.CONTROL_AWB_MODE_SHADE -> "Shade"
                        CameraCharacteristics.CONTROL_AWB_MODE_DAYLIGHT -> "Daylight"
                        CameraCharacteristics.CONTROL_AWB_MODE_TWILIGHT -> "Twilight"
                        CameraCharacteristics.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT -> "Cloudy Daylight"
                        CameraCharacteristics.CONTROL_AWB_MODE_FLUORESCENT -> "Fluorescent"
                        CameraCharacteristics.CONTROL_AWB_MODE_INCANDESCENT -> "Incandescent"
                        CameraCharacteristics.CONTROL_AWB_MODE_WARM_FLUORESCENT -> "Warm Fluorescent"
                        else -> "Unknown"
                    }
                }
                val awbModes = UserDeviceDetailsProperty("Auto White Balance Modes", awbModesNames.joinToString())
                cameraDetails.add(awbModes)
            }

            val maeRegionsValue = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE)
            val maeRegions = UserDeviceDetailsProperty("Maximum Auto Exposure Regions", maeRegionsValue.toString())
            cameraDetails.add(maeRegions)

            val mafRegionsValue = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF)
            val mafRegions = UserDeviceDetailsProperty("Maximum Auto Focus Regions", mafRegionsValue.toString())
            cameraDetails.add(mafRegions)

            val mawbRegionsValue = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE)
            val mawbRegions = UserDeviceDetailsProperty("Maximum Auto White Balance Regions", mawbRegionsValue.toString())
            cameraDetails.add(mawbRegions)

            val edgeModesValue = characteristics.get(CameraCharacteristics.EDGE_AVAILABLE_EDGE_MODES)
            edgeModesValue?.let {
                val edgeModesNames = it.map{ edgeMode ->
                    when(edgeMode) {
                        CameraCharacteristics.EDGE_MODE_OFF -> "Off"
                        CameraCharacteristics.EDGE_MODE_FAST -> "Fast"
                        CameraCharacteristics.EDGE_MODE_HIGH_QUALITY -> "High Quality"
                        CameraCharacteristics.EDGE_MODE_ZERO_SHUTTER_LAG -> "Zero Shutter Lag"
                        else -> "Unknown"
                    }
                }
                val edgeModes = UserDeviceDetailsProperty("Edge Modes", edgeModesNames.joinToString())
                cameraDetails.add(edgeModes)
            }

            val flashAvailableValue = if(characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true) "Yes" else "No"
            val flashAvailable = UserDeviceDetailsProperty("Flash Available", flashAvailableValue)
            cameraDetails.add(flashAvailable)

            val hotPixelModesValue = characteristics.get(CameraCharacteristics.HOT_PIXEL_AVAILABLE_HOT_PIXEL_MODES)
            hotPixelModesValue?.let {
                val hotPixelModesNames = it.map { hotPixelMode ->
                    when(hotPixelMode) {
                        CameraCharacteristics.HOT_PIXEL_MODE_OFF -> "Off"
                        CameraCharacteristics.HOT_PIXEL_MODE_FAST -> "Fast"
                        CameraCharacteristics.HOT_PIXEL_MODE_HIGH_QUALITY -> "High Quality"
                        else -> "Unknown"
                    }
                }
                val hotPixelModes = UserDeviceDetailsProperty("Hot Pixel Modes", hotPixelModesNames.joinToString())
                cameraDetails.add(hotPixelModes)
            }

            val thumbnailSizes = characteristics.get(CameraCharacteristics.JPEG_AVAILABLE_THUMBNAIL_SIZES)

            cameraDetails
        } catch(e: Exception) {
            Log.e("getCameraInfo", "${e.message}")
            cameraDetails
        }
    }
}