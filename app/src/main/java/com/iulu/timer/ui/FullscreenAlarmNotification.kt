package com.iulu.timer.ui

import android.content.BroadcastReceiver
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.iulu.timer.databinding.ActivityFullscreenAlarmNotificationBinding
import com.iulu.timer.timer.*

class FullscreenAlarmNotification : AppCompatActivity() {
    private lateinit var binding: ActivityFullscreenAlarmNotificationBinding
    private lateinit var actionAlarmUpdateBroadcastReceiver: BroadcastReceiver
    private var buttonStopBroadcastReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        binding = ActivityFullscreenAlarmNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        turnScreenOnAndKeyguardOff()
        actionAlarmUpdateBroadcastReceiver = addBroadcast(action = ALARM_EVENT_UPDATE) { timeElapsed ->
            binding.textView.text = timeElapsed
        }

        binding.stopButton.setOnClickListener {
           sendBroadcast(action = ALARM_EVENT_BUTTON_STOP)
        }
        buttonStopBroadcastReceiver = addBroadcast(action = ALARM_EVENT_BUTTON_STOP) {
            finish()
        }
    }

    override fun onDestroy() {
        turnScreenOffAndKeyguardOn()
        unregisterReceiver(actionAlarmUpdateBroadcastReceiver)
        unregisterReceiver(buttonStopBroadcastReceiver)
        super.onDestroy()
    }
}