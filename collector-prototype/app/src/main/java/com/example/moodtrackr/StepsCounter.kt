package com.example.moodtrackr

import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.content.Context
import android.hardware.SensorEvent
import androidx.fragment.app.FragmentActivity
import android.util.Log
import androidx.fragment.app.Fragment


class StepsCounter(activity: FragmentActivity) : Fragment(), SensorEventListener {

    init {

        var ctx : Context? = activity.applicationContext
        if (ctx == null) {
            Log.d("StepsCounter", "Context Failed")
            throw Exception("Context Failed")
        }
        val sensorManager : SensorManager? = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManager == null) {
            Log.d("StepsCounter", "Sensor Manager Failed")
            throw Exception("Sensor Manager Failed")
        }
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            ?: throw Exception("Got no step sensor")

        var startCount = 0
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        var x = 0
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.d("DEBUG", event!!.values[0].toString())
    }

}