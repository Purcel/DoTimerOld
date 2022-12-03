package com.iulu.timer.ui

import android.os.*
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.android.material.button.MaterialButton
import com.iulu.timer.R
import com.iulu.timer.databinding.ActivityMainBinding
import com.iulu.timer.timer.Timer
import com.iulu.timer.timer.TimerPref

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}