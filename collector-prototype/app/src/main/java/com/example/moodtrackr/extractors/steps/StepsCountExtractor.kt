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
            stepsUponLaunch = event!!.values[0]
        }
        steps = event!!.values[0]
        //Log.e("DEBUG", "${steps}, ${stepsDBLastUpdate}")
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
        var stepsDBLastUpdate: Float = 0F

        var stepsUponLaunch: Float = 0F

        fun calcStepsToBeAdded(): Long {
            return (steps - stepsDBLastUpdate - stepsUponLaunch).toLong()
        }

        fun resetStepCountExtractor() {
            stepsUponLaunch = -steps
            steps = 0F
            stepsDBLastUpdate = 0F
        }

        fun calcSteps(context: Context) {
            CoroutineScope(Dispatchers.Default).launch {
                var stepsDB = DatabaseManager.getInstance(context).rtUsageRecordsDAO.getObjOnDay(
                    DatesUtil.getTodayTruncated().time
                )
                var stepsAdd = calcStepsToBeAdded()
                stepsDB = checkSequence(context, stepsDB)
                stepsDB.steps += stepsAdd
                DatabaseManager.getInstance(context).rtUsageRecordsDAO.update( stepsDB )
            }
        }

        private fun checkSequence(context: Context, stepsDB: RTUsageRecord?): RTUsageRecord {
            var stepsDBNew : RTUsageRecord
            runBlocking {
                if (stepsDB == null) {
                    //clean()
                    //registerListener()
                    stepsDBNew = RTUsageRecord(
                        DatesUtil.getTodayTruncated(),
                        0,
                        0
                    )
                    resetStepCountExtractor()

                    DatabaseManager.getInstance(context).rtUsageRecordsDAO.insertAll(stepsDBNew)
                }
                else {
                    stepsDBNew = stepsDB
                }
            }
            return stepsDBNew
        }
    }
}