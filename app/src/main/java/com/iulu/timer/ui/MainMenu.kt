package com.iulu.timer.ui

import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.iulu.billing.Billing
import com.iulu.timer.R

fun mainMenuDispatcher(menu: MenuItem, fragment: Fragment) {
    when(menu.itemId) {
        R.id.contrib -> {
            Billing(fragment.requireActivity()).startTheFlow()
        }
        R.id.about -> {
            menu.setOnMenuItemClickListener {
                val action = MainFragmentDirections.actionMainFragmentToAboutFragment();
                fragment.findNavController().navigate(action)
                false
            }
        }
    }
}