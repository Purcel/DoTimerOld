package com.iulu.timer.timer

import android.content.Context
import androidx.annotation.DrawableRes
import com.iulu.timer.ui.TimerState

object TimerPref {

    fun saveWakeupTime(context: Context, time: Long, runInBackground: Boolean = true) {
        context.getSharedPreferences(PREF_KEY, 0).edit()
            .apply {
                putLong(TIMER_PREF_KEY, time)
                if (runInBackground)
                    apply()
                else
                    commit()
            }
    }

    fun restoreWakeupTime(context: Context): Long =
        context.getSharedPreferences(PREF_KEY, 0)
            .getLong(TIMER_PREF_KEY, 0L)

    fun savePauseTime(context: Context, time: Long, runInBackground: Boolean = true) {
        context.getSharedPreferences(PREF_KEY, 0).edit()
            .apply {
                putLong(TIMER_COUNTDOWN_TIME_KEY, time)
                if (runInBackground)
                    apply()
                else
                    commit()
            }
    }

    fun restorePauseTime(context: Context): Long =
        context.getSharedPreferences(PREF_KEY, 0)
            .getLong(TIMER_COUNTDOWN_TIME_KEY, 0L)

    fun saveTimerState(context: Context, state: TimerState) {
        context.getSharedPreferences(PREF_KEY, 0).edit()
            .apply {
                putString(TIMER_STATE_KEY, state.name)
                apply()

            }
    }

    fun restoreTimerState(context: Context): TimerState {
        val result = context.getSharedPreferences(PREF_KEY, 0)
            .getString(TIMER_STATE_KEY, TimerState.INIT.name)
        return when(result) {
            TimerState.INIT.name -> TimerState.INIT
            TimerState.RUNNING.name -> TimerState.RUNNING
            TimerState.PAUSE.name -> TimerState.PAUSE
            TimerState.ALARM.name -> TimerState.ALARM
            else -> TimerState.INIT
        }
    }

    fun saveCheckedIcon(context: Context, @DrawableRes resId: Int) {
        context.getSharedPreferences(PREF_KEY, 0).edit()
            .apply {
                putInt(TIMER_CHECKED_ICON_KEY, resId)
                apply()
            }
    }

    @DrawableRes
    fun restoreCheckedIcon(context: Context, defId: Int = 0): Int =
         context.getSharedPreferences(PREF_KEY, 0)
            .getInt(TIMER_CHECKED_ICON_KEY, defId)

    fun firstStart(context: Context, block: () -> Unit) {
        val pref = context.getSharedPreferences(PREF_KEY, 0)
        if (pref.getBoolean(TIMER_FIRST_START_KEY, true))
            block()
        pref.edit().apply {
                putBoolean(TIMER_FIRST_START_KEY, false)
                apply()
        }
    }

    fun saveActionClass(context: Context, kClass: String) {
        context.getSharedPreferences(PREF_KEY, 0).edit()
            .apply {
                putString(TIMER_ACTION_KEY, kClass)
                apply()
            }
    }

    fun getActionClass(context: Context, default: String = ""): String? =
        context.getSharedPreferences(PREF_KEY, 0)
            .getString(TIMER_ACTION_KEY, default)


}

const val PREF_KEY = "PREF"
const val TIMER_PREF_KEY = "TIMER_PREF"
const val TIMER_COUNTDOWN_TIME_KEY = "TIMER_COUNTDOWN_TIME"
const val TIMER_STATE_KEY = "TIMER_STATE"
const val TIMER_FIRST_START_KEY = "APP_FIRST_START"
const val TIMER_ACTION_KEY = "TIMER_ACTION"
const val TIMER_CHECKED_ICON_KEY = "TIMER_CHECKED_ICON"