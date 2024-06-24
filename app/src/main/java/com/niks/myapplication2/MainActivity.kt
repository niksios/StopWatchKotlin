package com.niks.myapplication2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niks.myapplication2.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isStarted = false
    private lateinit var serviceIntent : Intent
    private var time = 0.0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        /*val view = binding.root
        setContentView(view)*/
        setContentView(binding.root)
        //setContentView(R.layout.activity_main)

        binding.btnStart.setOnClickListener {
            startOrStop()
        }

        binding.btnReset.setOnClickListener {
            reset()
        }

        serviceIntent = Intent(applicationContext, StopWatchService::class.java)
        registerReceiver(updateTime, IntentFilter(StopWatchService.UPDATED_TIME),
            RECEIVER_NOT_EXPORTED
        )

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(updateTime, IntentFilter(StopWatchService.UPDATED_TIME),
                RECEIVER_NOT_EXPORTED
            )
        }*/

    }

    private fun startOrStop() {

        if (isStarted) {
            stop()
        } else {
            start()
        }
    }

    private fun start() {
        serviceIntent.putExtra(StopWatchService.CURRENT_TIME, time)
        startService(serviceIntent)

        binding.btnStart.text = "STOP"
        isStarted = true
    }

    private fun stop() {
        stopService(serviceIntent)

        binding.btnStart.text = "START"
        isStarted = false

    }

    private fun reset() {
        stop()
        time = 0.0
        binding.tvTime.text = getFormattedTime(time)

    }

    private val updateTime : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(StopWatchService.CURRENT_TIME, 0.0)
            //binding.tvTime.text = time.toString()
            binding.tvTime.text = getFormattedTime(time)
        }

    }

    private fun getFormattedTime(time: Double):String{
        val timeInt = time.roundToInt()
        val hours = timeInt % 86400 / 3600
        val minutes = timeInt % 86400 % 3600 / 60
        val seconds = timeInt % 86400 % 3600 % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)

    }


}