package com.example.moodtrackr.extractors.steps

import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.content.Context
import android.hardware.SensorEvent
import androidx.fragment.app.FragmentActivity
import android.util.Log
import com.example.moodtrackr.collectors.db.DBHelperRT
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.util.DatabaseManager
import com.example.moodtrackr.util.DatesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking



class StepsCountExtractor(context: Context) : SensorEventListener {
    private lateinit var context : Context
    lateinit var sensorManager: SensorManager

    constructor(activity: FragmentActivity): this(activity.applicationContext)
    init {
        this.context = context
        this.sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        registerListener()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!initialized) {
            initialized = true
            stepsLastUpdate = event!!.values[0]
        }
        steps = event!!.values[0]
//        Log.e("STEPS_COUNT_EXT_STEP", "${steps}, ${stepsLastUpdate}")
//        stepsTrue = event!!.values[0]
//        accurateUpdateSequence()
    }

    fun registerListener() {
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            ?: throw Exception("Got no step sensor")
        Log.e("DEBUG", "Is wake up sensor?: ${sensor!!.isWakeUpSensor}")
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun clean() {
        sensorManager.unregisterListener(this)
    }

    companion object {
        var initialized: Boolean = false
        var steps: Float = 0F
        var stepsLastUpdate: Float = 0F

        fun calcStepsToBeAdded(): Long {
            return (steps - stepsLastUpdate).toLong()
        }

        fun resetStepCountExtractor() {
            stepsLastUpdate = steps
        }

        fun stepsChange(stepsDB: Long): Long {
            var stepsAdd = calcStepsToBeAdded()
            stepsLastUpdate = steps
            val stepsDBUpdate = stepsDB + stepsAdd
            Log.e("STEPS_COUNT_EXT", "DB: $stepsDB, stepsAdd: $stepsAdd, stepsDBUpdate: $stepsDBUpdate")
            return stepsDBUpdate
        }
    }
}