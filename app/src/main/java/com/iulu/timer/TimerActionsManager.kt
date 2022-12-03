package com.iulu.timer

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.iulu.timer.timer.*
import com.iulu.timer.timer.TimerNotification.showNotification
import com.iulu.timer.ui.TimerState

const val ACTION_CLASS_NAME = "ACTION_CLASS_NAME"
const val MEMBER_FIRE = "fire"
const val MEMBER_NOTIFICATION_BUTTON_PRESSED = "notificationButtonPressed"
const val TIMER_ACTION_FIRE = "FIRE"
const val TIMER_ACTION_TYPE_NOTIFY = "TIMER_ACTION_TYPE_NOTIFY"
const val TIMER_ACTION_TYPE_ALARM = "TIMER_ACTION_TYPE_ALARM"

interface TimerAction {
    fun fire(context: Context)
    fun notificationButtonPressed(context: Context) {}
}

object TimerActionManager {
    fun startAlarm(context: Context, timerAction: TimerAction) {
        TimerPref.saveTimerState(context, TimerState.ALARM)
        val intent = Intent(context, AlarmService::class.java)//.apply {
        intent.putExtra(ACTION_CLASS_NAME, timerAction::class.qualifiedName)
        val s = context.startForegroundService(intent)
        Toast.makeText(context, s.toString(), Toast.LENGTH_SHORT).show()
        context.sendBroadcast(TIMER_ACTION_TYPE_ALARM, action = TIMER_ACTION_FIRE)
    }

    fun stopAlarm(context: Context) {
        context.sendBroadcast(action = ALARM_EVENT_BUTTON_STOP)
    }

    fun showNotification(context: Context,
                         title: String,
                         description: String,
                         buttonText: String? = null,
                         @DrawableRes buttonDrawable: Int = 0) {
        TimerPref.saveTimerState(context, TimerState.INIT)
        context.sendBroadcast(TIMER_ACTION_TYPE_NOTIFY, action = TIMER_ACTION_FIRE)
        context.apply {
            showNotification(title, description,
                NotificationActionButton(buttonText, buttonDrawable)
            )
        }
    }
}

object TimerActionStopMusic : TimerAction {
    //TODO ADD compatibility
    @RequiresApi(Build.VERSION_CODES.O)
    override fun fire(context: Context) {
        val handler = Handler(Looper.getMainLooper())
        val audioManager = context.getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
            setAudioAttributes(AudioAttributes.Builder().run {
                //TODO Check if these Attributes works on every device and every app
                setUsage(AudioAttributes.USAGE_UNKNOWN)
                setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                build()
            })
            setAcceptsDelayedFocusGain(true)
            setOnAudioFocusChangeListener({}, handler)
            build()
        }
        val res = audioManager.requestAudioFocus(focusRequest)
        val lock = Any()
        synchronized(lock){
           when(res) {
                AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {}
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                    TimerActionManager.showNotification(context,
                        "Music stopped",
                        "This is music stopped action descriptor")
                }
                AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {}
            }
        }
    }
}

object TimerActionAlarm : TimerAction {
    override fun fire(context: Context) {
        TimerActionManager.startAlarm(context, this@TimerActionAlarm)
    }
    override fun notificationButtonPressed(context: Context) {
        TimerActionManager.stopAlarm(context)
    }
}

object TimerActionFlashLight : TimerAction {
    override fun fire(context: Context) {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                cameraManager.setTorchMode("0", true)
            } catch (_: CameraAccessException) {}
        TimerActionManager.showNotification(context,
            "Flashlight on",
            "The flashlight is on!", "Turn OFF",  R.drawable.ic_button_stop)
    }

    override fun notificationButtonPressed(context: Context) {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraManager.setTorchMode("0", false)
        } catch (_: CameraAccessException) {}
    }
}
