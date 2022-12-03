package com.iulu.timer.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import com.google.android.material.chip.Chip

@SuppressLint("CustomViewStyleable", "PrivateResource")
class TimerChip : Chip {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val tp = context.obtainStyledAttributes(attributeSet, com.google.android.material.R.styleable.Chip)
        chipIconRes = tp.getResourceId(com.google.android.material.R.styleable.Chip_chipIcon, 0)
        tp.recycle()
    }
    @DrawableRes
    var chipIconRes: Int = 0
    private set
}