package com.example.moodtrackr.extractors

import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.content.Context
import android.hardware.SensorEvent
import androidx.fragment.app.FragmentActivity
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.utilities.DatabaseManager
import com.example.moodtrackr.utilities.DatesUtil
import kotlinx.coroutines.runBlocking



class StepsCountExtractor(context: Context) : Fragment(), SensorEventListener {
    private lateinit var context : Context
    private var stepsDBLastUpdate: Long = 0

    constructor(activity: FragmentActivity): this(activity.applicationContext)
    init {
        this.context = context
        if (context == null) {
            Log.d("StepsCounter", "Context Failed")
            throw Exception("Context Failed")
        }
        val sensorManager : SensorManager? = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManager == null) {
            Log.d("StepsCounter", "Sensor Manager Failed")
            throw Exception("Sensor Manager Failed")
        }
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            ?: throw Exception("Got no step sensor")

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        var x = 0
    }

    override fun onSensorChanged(event: SensorEvent?) {
        steps = event!!.values[0].toLong()
        Log.d("DEBUG", event!!.values[0].toString())
    }

    private fun updateSequence() {
        if ( steps - stepsDBLastUpdate  > 100 ) {
            updateDB( steps )
        }
    }

    private fun updateDB(steps: Long) {
        runBlocking {
            var stepsDB = DatabaseManager.rtUsageRecordsDAO.getStepsObjOnDay(
                DatesUtil.getTodayTruncated().time
            )
            stepsDB = checkSequence(stepsDB)
            stepsDB.usageVal = steps.toString()
            DatabaseManager.rtUsageRecordsDAO.update( stepsDB )
            stepsDBLastUpdate = steps
        }
    }

    private fun checkSequence(stepsDB: RTUsageRecord?): RTUsageRecord {
        var stepsDBNew : RTUsageRecord? = stepsDB
        runBlocking {
            if (stepsDB === null) {
                stepsDBNew = RTUsageRecord(
                    DatesUtil.getTodayTruncated(),
                    "steps",
                    "0"
                )
                DatabaseManager.rtUsageRecordsDAO.insertAll(stepsDBNew!!)
            }
        }
        return stepsDBNew!!
    }

    companion object {
        var steps: Long = 0
    }
}