package com.overandoutnerd.deviceinfo.home.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SensorsRepository(private val context: Context) {
    suspend fun getSensors(): List<SimpleSensorDetails> {
        return withContext(Dispatchers.IO) {
            val sensorsList = mutableListOf<SimpleSensorDetails>()
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            try {
                val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
                for (sensor in sensors) {
                    sensorsList.add(sensor.toSimpleSensorDetails())
                }
                sensorsList
            } catch (e: Exception) {
                mutableListOf()
            }
        }
    }
}

private fun Sensor.toSimpleSensorDetails(): SimpleSensorDetails {
    val category = SensorCategory.forType(type)
    return SimpleSensorDetails(
        name = name,
        vendor = vendor,
        type = stringType,
        typeConstant = type,
        version = version,
        power = power,
        maxRange = maximumRange,
        resolution = resolution,
        minDelayMicros = minDelay,
        maxDelayMicros = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) maxDelay else 0,
        reportingMode = reportingMode,
        isWakeUpSensor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) isWakeUpSensor else false,
        isDynamicSensor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) isDynamicSensor else false,
        id = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) id else 0,
        category = category
    )
}

data class SimpleSensorDetails(
    var name: String,
    var vendor: String,
    var type: String,
    var typeConstant: Int,
    var version: Int,
    var power: Float,
    var maxRange: Float,
    var resolution: Float,
    var minDelayMicros: Int,
    var maxDelayMicros: Int,
    var reportingMode: Int,
    var isWakeUpSensor: Boolean,
    var isDynamicSensor: Boolean,
    var id: Int,
    var category: SensorCategory
)

enum class SensorCategory(val icon: ImageVector) {
    MOTION(Icons.Rounded.Vibration),
    ENVIRONMENTAL(Icons.Rounded.Thermostat),
    POSITION(Icons.Rounded.Explore),
    OTHER(Icons.Rounded.Sensors);

    companion object {
        private val motionTypes = setOf(
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_GRAVITY,
            Sensor.TYPE_LINEAR_ACCELERATION,
            Sensor.TYPE_SIGNIFICANT_MOTION,
            Sensor.TYPE_STEP_DETECTOR,
            Sensor.TYPE_STEP_COUNTER,
            Sensor.TYPE_GAME_ROTATION_VECTOR,
            Sensor.TYPE_GYROSCOPE_UNCALIBRATED
        )
        @Suppress("DEPRECATION")
        private val environmentalTypes = setOf(
            Sensor.TYPE_LIGHT,
            Sensor.TYPE_PRESSURE,
            Sensor.TYPE_TEMPERATURE,
            Sensor.TYPE_PROXIMITY,
            Sensor.TYPE_RELATIVE_HUMIDITY,
            Sensor.TYPE_AMBIENT_TEMPERATURE,
            Sensor.TYPE_HEART_RATE
        )
        @Suppress("DEPRECATION")
        private val positionTypes = setOf(
            Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_ORIENTATION,
            Sensor.TYPE_ROTATION_VECTOR,
            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
            Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED
        )

        fun forType(type: Int): SensorCategory = when (type) {
            in motionTypes -> MOTION
            in environmentalTypes -> ENVIRONMENTAL
            in positionTypes -> POSITION
            else -> OTHER
        }
    }
}

fun reportingModeLabel(mode: Int): String = when (mode) {
    Sensor.REPORTING_MODE_CONTINUOUS -> "Continuous"
    Sensor.REPORTING_MODE_ON_CHANGE -> "On change"
    Sensor.REPORTING_MODE_ONE_SHOT -> "One-shot"
    Sensor.REPORTING_MODE_SPECIAL_TRIGGER -> "Special trigger"
    else -> "Unknown"
}
