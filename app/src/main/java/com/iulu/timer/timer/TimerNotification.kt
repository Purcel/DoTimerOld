package com.iulu.timer.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.iulu.timer.ui.MainActivity
import com.iulu.timer.R
import com.iulu.timer.ui.FullscreenAlarmNotification

object TimerNotification {

    fun Context.createNotificationChannel(id: String, name: String, description: String, importance: Int) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, name, importance).apply {
                this.description = description
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getNotificationContentIntent(context: Context): PendingIntent {
        val notificationBodyIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent
            .getActivity(context, 0, notificationBodyIntent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val mIntent = Intent(context, NotificationActionButtonReceiver::class.java)
        return PendingIntent.getBroadcast(context, 0, mIntent, PendingIntent.FLAG_IMMUTABLE)
    }

    fun Context.showNotification(
        title: String,
        description: String,
        actionButton: NotificationActionButton? = null
    ): Notification {

        val buttonAction = if (actionButton != null) NotificationCompat.Action.Builder(
            actionButton.buttonDrawable,
            actionButton.buttonText,
            getPendingIntent(this)).build()
        else null

        val builder = NotificationCompat.Builder(this, CHANNEL_ID).run {
            setSmallIcon(R.drawable.ic_timer_notification)
            setContentTitle(title)
            setContentText(description)
            setContentIntent(getNotificationContentIntent(this@showNotification))
            setCategory(Notification.CATEGORY_ALARM)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setAutoCancel(true)
            if (buttonAction != null)
                addAction(buttonAction)
            build()
        }

        return with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder)
            builder
        }
    }

    fun Context.buildAlarmNotification(title: String,
                                       description: String,
                                       actionButton: NotificationActionButton? =
                                           NotificationActionButton("STOP??", R.drawable.ic_button_stop)
    ): Notification {
        val buttonAction = if (actionButton != null) NotificationCompat.Action.Builder(
            actionButton.buttonDrawable,
            actionButton.buttonText,
            getPendingIntent(this)).build()
        else null

        val fullscreenIntent = Intent(this, FullscreenAlarmNotification::class.java)
        val fullscreenPendingIntent = PendingIntent.getActivity(this, 0, fullscreenIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)

        return NotificationCompat.Builder(this, CHANNEL_ID_ALARM).run {
            setSmallIcon(R.drawable.ic_timer_notification)
            setContentTitle(title)
            setContentText(description)
            color = getColor(R.color.alarm_notification_color)
            setColorized(true)
            //On platforms Build.VERSION_CODES.O and above this value is ignored
            priority = NotificationCompat.PRIORITY_HIGH
            //On platforms Build.VERSION_CODES.O and above this value is ignored
            foregroundServiceBehavior = NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
            setContentIntent(getNotificationContentIntent(this@buildAlarmNotification))
            setFullScreenIntent(fullscreenPendingIntent, true)
            if (buttonAction != null)
                addAction(buttonAction)
            setCategory(Notification.CATEGORY_ALARM)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setOngoing(true)
            setAutoCancel(true)
            build()
        }
    }

    fun Context.showAlarmNotification(notification: Notification) {
        with(NotificationManagerCompat.from(this)) {
            notify(ALARM_NOTIFICATION_ID, notification)
        }
    }

}

data class NotificationActionButton(
    val buttonText: String? = null,
    @DrawableRes val buttonDrawable: Int = 0)

const val CHANNEL_ID = "CHANNEL_ID"
const val CHANNEL_ID_ALARM = "CHANNEL_ID_ALARM"
const val ALARM_NOTIFICATION_ID = 1
const val NOTIFICATION_ID = 2

