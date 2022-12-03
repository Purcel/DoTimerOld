package com.iulu.timer.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlin.reflect.full.functions


fun Context.sendBroadcast(string: String = "", action: String) {
    sendBroadcast(Intent(action).apply { putExtra("STRING", string) })
}

fun Context.addBroadcast(action: String, block: (String) -> Unit): BroadcastReceiver {
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            block(intent?.getStringExtra("STRING") ?: "")
        }
    }
    registerReceiver(receiver, IntentFilter(action))
    return receiver
}

fun String.callObj(member: String, context: Context, vararg params: Any): Any? =
    Class.forName(this).kotlin.run {
        functions.last { it.name == member }.call(objectInstance, context, *params)
    }