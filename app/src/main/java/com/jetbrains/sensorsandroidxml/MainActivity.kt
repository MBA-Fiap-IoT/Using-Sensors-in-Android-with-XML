package com.jetbrains.sensorsandroidxml

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var txtMaximumRange: TextView
    private lateinit var txtPower: TextView
    private lateinit var txtValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        var items: List<String> = ArrayList()

        deviceSensors.forEach {
            items = items.plus(it.name)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        val autoComplete: AutoCompleteTextView? = findViewById(R.id.textField)
        autoComplete?.setAdapter(adapter)
        autoComplete?.setOnItemClickListener { parent, _, index, _ ->
            var name: String = parent.adapter.getItem(index) as String
            var sensorChoose: Sensor = deviceSensors.first { sensor -> sensor.name.equals(name)  }
            txtMaximumRange.text = "Maximum Range: ${sensorChoose.maximumRange}"
            txtPower.text = "Power: ${sensorChoose.power}"

            sensorManager.unregisterListener(this)
            sensorManager.registerListener(
                this,
                sensorChoose,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        txtPower = findViewById(R.id.txtPower)
        txtValue = findViewById(R.id.txtValue)
        txtMaximumRange = findViewById(R.id.txtMaximumRange)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.values?.let {
            txtValue.text = "${it[0]}"

            if (it.size == 1) {
                txtValue.text = "[${it[0]}][][]"
            } else {
                txtValue.text = "[${it[0]}][${it[1]}][${it[2]}]"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}