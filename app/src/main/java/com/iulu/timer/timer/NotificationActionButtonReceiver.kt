package com.iulu.timer.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.iulu.timer.MEMBER_NOTIFICATION_BUTTON_PRESSED

class NotificationActionButtonReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        TimerPref.getActionClass(context)?.callObj(MEMBER_NOTIFICATION_BUTTON_PRESSED, context)
    }
}