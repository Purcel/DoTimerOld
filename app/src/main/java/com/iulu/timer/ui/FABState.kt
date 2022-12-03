package com.iulu.timer.ui

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.iulu.timer.R

enum class FABState(@DrawableRes val id: Int) {
    START(R.drawable.ic_button_start),
    PAUSE(R.drawable.ic_button_pause),
    STOP(R.drawable.ic_button_stop);
}

fun FloatingActionButton.setState(context: Context, state: FABState) {
    setImageDrawable(AppCompatResources.getDrawable(context, state.id))
}