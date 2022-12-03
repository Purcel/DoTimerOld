package com.iulu.dialpicker

import android.content.Context
import android.os.Build.VERSION_CODES.*
import android.os.Build.VERSION.*
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object DialHaptics {

    fun click(context: Context) {
        val vib: Vibrator
        val vibratorManager: VibratorManager

        if (SDK_INT <= R)
            vib = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        else {
            vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vib = vibratorManager.defaultVibrator
        }

        if (SDK_INT <= N) //?
            vib.vibrate(30)
        else if (SDK_INT >= O && SDK_INT <= P) //Tested
            vib.vibrate(VibrationEffect.createOneShot(30, 255))
        else if (SDK_INT >= Q && SDK_INT <= R) //Tested
            vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        else if (SDK_INT >= S) //?
            vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
    }
}