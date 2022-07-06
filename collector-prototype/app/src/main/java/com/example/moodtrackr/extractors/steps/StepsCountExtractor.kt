package com.example.moodtrackr.extractors.steps

import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.content.Context
import android.hardware.SensorEvent
import androidx.fragment.app.FragmentActivity
import android.util.Log
import com.example.moodtrackr.collectors.db.DBHelperRT
import com.example.moodtrackr.collectors.service.DataCollectorService
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
        if (context == null) {
            Log.d("StepsCounter", "Context Failed")
            throw Exception("Context Failed")
        }
        this.sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManager == null) {
            Log.d("StepsCounter", "Sensor Manager Failed")
            throw Exception("Sensor Manager Failed")
        }

        steps = DBHelperRT.getStepsSafe(context, DatesUtil.getTodayTruncated())
//        steps = DataCollectorService.steps
        registerListener()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        steps += 1
        //Log.e("DEBUG", "${steps}, ${stepsDBLastUpdate}")
        updateSequence()
    }

    private fun updateSequence() {
        if ( steps - stepsDBLastUpdate  > 10 ) {
            updateDB( steps )
        }
    }

    private fun updateDB(steps: Long) {
        CoroutineScope(Dispatchers.Default).launch {
            var stepsDB = DatabaseManager.getInstance(context).rtUsageRecordsDAO.getObjOnDay(
                DatesUtil.getTodayTruncated().time
            )
            stepsDB = checkSequence(stepsDB)
            stepsDB.steps = steps
            DatabaseManager.getInstance(context).rtUsageRecordsDAO.update( stepsDB )
            stepsDBLastUpdate = steps
            DataCollectorService.localSteps = steps
        }
    }

    private fun checkSequence(stepsDB: RTUsageRecord?): RTUsageRecord {
        var stepsDBNew : RTUsageRecord
        runBlocking {
            if (stepsDB === null) {
                clean()
                registerListener()
                stepsDBNew = RTUsageRecord(
                    DatesUtil.getTodayTruncated(),
                    0,
                    0
                )
                DatabaseManager.getInstance(context).rtUsageRecordsDAO.insertAll(stepsDBNew)
            }
            else {
                stepsDBNew = stepsDB
            }
        }
        return stepsDBNew
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
        var steps: Long = 0
        var stepsDBLastUpdate: Long = 0
    }
}