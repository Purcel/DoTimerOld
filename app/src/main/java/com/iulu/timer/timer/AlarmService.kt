package com.iulu.timer.timer

import android.app.Notification
import android.app.Service
import android.content.*
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.iulu.timer.ui.TimerState
import com.iulu.timer.timer.TimerNotification.buildAlarmNotification
import com.iulu.timer.timer.TimerNotification.showAlarmNotification

class AlarmService : Service() {
    private var notification: Notification? = null
    private var buttonStopBroadcastReceiver: BroadcastReceiver? = null

    var timeMillis = 0L
    private set

    private var firstTime = true
    private val countDownTimer: CountDownTimer by lazy {
        object : CountDownTimer(ALARM_TIMEOUT, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeMillis = millisUntilFinished
                val elapsedTimeSeconds = ((timeMillis - ALARM_TIMEOUT)/1000)

                notification = this@AlarmService.buildAlarmNotification("$elapsedTimeSeconds", "Time's up")
                this@AlarmService.showAlarmNotification(notification as Notification)
                if (firstTime) {
                    startForeground(ALARM_NOTIFICATION_ID, notification)
                }
                firstTime = false
                baseContext.sendBroadcast(string = elapsedTimeSeconds.toString(), action = ALARM_EVENT_UPDATE)
            }

            override fun onFinish() {
                timeMillis = 0L
                firstTime = true
                baseContext.sendBroadcast(action = ALARM_EVENT_BUTTON_STOP)
            }
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        countDownTimer.start()
        buttonStopBroadcastReceiver = addBroadcast(action = ALARM_EVENT_BUTTON_STOP) {
            stopAlarm()
        }
        return START_STICKY
    }

    private fun stopAlarm() {
        countDownTimer.cancel()
        TimerPref.saveTimerState(this, TimerState.INIT)
        NotificationManagerCompat.from(this).cancel(ALARM_NOTIFICATION_ID)
        unregisterReceiver(buttonStopBroadcastReceiver)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}

const val ALARM_EVENT_BUTTON_STOP = "STOP"
const val ALARM_EVENT_UPDATE = "ALARM_EVENT_UPDATE"

private const val ALARM_TIMEOUT = 60_000L
