package com.iulu.timer.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.forEach
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.iulu.timer.R
import com.iulu.timer.databinding.ActivityMainBinding
import com.iulu.timer.databinding.FragmentMainBinding
import com.iulu.timer.timer.Timer
import com.iulu.timer.timer.TimerPref

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    private val currentTime: Long
        get() = System.currentTimeMillis()
    private var hour = 0L
    private var min = 0L
    private var sec = 0L
    private val dialPickerTime: Long
        get() = (hour*3600 + min*60 + sec)

    private val wakeUpTime: Long
        get() = currentTime + pauseTime

    private var pauseTime = 0L

    private var timerState: TimerState = TimerState.INIT
        set(value) {
            field = value
            when(value) {
                TimerState.INIT ->    {
                    binding.text.visibility = View.GONE
                    binding.dials.visibility = View.VISIBLE
                    binding.buttonStartPause.setState(requireContext(), FABState.START)
                    binding.buttonStop.isEnabled = false
                    binding.buttonActions.isEnabled = true
                }
                TimerState.RUNNING -> {
                    binding.dials.visibility = View.GONE
                    binding.text.visibility = View.VISIBLE
                    binding.buttonStartPause.setState(requireContext(), FABState.PAUSE)
                    binding.buttonStop.isEnabled = true
                    binding.buttonActions.isEnabled = false
                }
                TimerState.PAUSE ->   {
                    binding.dials.visibility = View.GONE
                    binding.text.visibility = View.VISIBLE
                    binding.buttonStartPause.setState(requireContext(), FABState.START)
                    binding.buttonStop.isEnabled = true
                    binding.buttonActions.isEnabled = true
                }
                TimerState.ALARM ->   {
                    binding.dials.visibility = View.GONE
                    binding.text.visibility = View.VISIBLE
                    binding.buttonStartPause.setState(requireContext(), FABState.STOP)
                    binding.buttonStop.isEnabled = true
                    binding.buttonActions.isEnabled = false
                }
            }
        }

    private val actionsBottomSheet = ActionsBottomSheet()

    private val bottomSheetCallback = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            super.onFragmentPaused(fm, f)
            if (f is ActionsBottomSheet)
                f.checkedChipIconId?.let { iconId ->
                    binding.buttonActions.icon = AppCompatResources.getDrawable(requireContext(), iconId)
                }
        }
    }

    private val timer: Timer by lazy { Timer(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        val menu = binding.menu.menu
        requireActivity().menuInflater.inflate(R.menu.main_menu, menu)
        menu.forEach { mainMenuDispatcher(it, this) }
        timer.initTimer()

        timer.alarmUpdateListener(
            tick = {
                binding.text.text = it
            },
            stopButton = {
                timerState = TimerState.INIT
            })

        timer.timerFireListener(
            typeNotify = {
                timerState = TimerState.INIT
            },
            typeAlarm = {
                timerState = TimerState.ALARM
            })

        timer.onTickLiveData.observe(requireActivity()) {
            pauseTime = it
            binding.text.text = requireActivity().fromSecondsToHhMmSs(pauseTime/1000)
        }

        dialsOnSnap {
            binding.buttonStartPause.isEnabled = (dialPickerTime >= 1)
        }

        binding.buttonStartPause.setOnClickListener {
            when (timerState) {
                TimerState.INIT, TimerState.PAUSE -> {
                    if (timerState == TimerState.INIT)
                        timer.startForegroundTimer(dialPickerTime*1000L)
                    else if (timerState == TimerState.PAUSE)
                        timer.startForegroundTimer(pauseTime)
                    timerState = TimerState.RUNNING
                }
                TimerState.RUNNING -> {
                    timer.stopForegroundTimer()
                    timerState = TimerState.PAUSE
                }
                TimerState.ALARM -> {
                    timer.stopAlarm()
                    timerState = TimerState.INIT
                }
            }
        }

        binding.buttonStop.setOnClickListener {
            if (timerState == TimerState.RUNNING || timerState == TimerState.PAUSE || timerState == TimerState.ALARM) {
                timerState = TimerState.INIT
                timer.stopForegroundTimer()
                timer.stopAlarm()
            }
        }

        binding.buttonActions.setOnClickListener {
            //TODO DoubleTap crash the app -Fragment already added: ActionsBottomSheet{a9be8e2}

            actionsBottomSheet.show(parentFragmentManager, ActionsBottomSheet.TAG)
        }

        parentFragmentManager.registerFragmentLifecycleCallbacks(bottomSheetCallback, false)
        restoreCheckedActionIcon(binding.buttonActions)

        timerState = TimerPref.restoreTimerState(requireContext())
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        timer.unregisterTimer()
    }

    override fun onStart() {
        restoreTimerState(timerState)
        super.onStart()
    }

    override fun onPause() {
        saveTimerState()
        super.onPause()
    }

    private fun restoreCheckedActionIcon(materialButton: MaterialButton) {
        val resId = TimerPref.restoreCheckedIcon(requireContext(), R.drawable.ic_music_off)
        materialButton.icon = AppCompatResources.getDrawable(requireContext(), resId)
    }

    private fun saveTimerState() {
        when(timerState) {
            TimerState.INIT -> {}
            TimerState.PAUSE -> {
                TimerPref.savePauseTime(requireContext(), pauseTime)
            }
            TimerState.RUNNING -> {
                timer.setBackgroundTimer(wakeUpTime)
                timer.stopForegroundTimer()
            }
            TimerState.ALARM -> {}
        }
        TimerPref.saveTimerState(requireContext(), timerState)
    }

    private fun restoreTimerState(timerState: TimerState) {
        when(timerState) {
            TimerState.INIT -> {}
            TimerState.RUNNING -> {
                timer.removeBackgroundTimer()
                val time = TimerPref.restoreWakeupTime(requireContext()) - currentTime
                timer.startForegroundTimer(time)
            }
            TimerState.PAUSE -> {
                pauseTime = TimerPref.restorePauseTime(requireContext())
                binding.text.text = requireActivity().fromSecondsToHhMmSs(pauseTime/1000)
            }
            TimerState.ALARM -> {}
        }
    }

    private inline fun dialsOnSnap(crossinline block: () -> Unit) {
        binding.dialHour.run {
            list = twentyFour
            setOnSnapListener {
                hour = it.toLong()
                block()
            }
        }
        binding.dialMin.run {
            list = sixty
            setOnSnapListener {
                min = it.toLong()
                block()
            }
        }
        binding.dialSec.run {
            list = sixty
            setOnSnapListener {
                sec = it.toLong()
                block()
            }
        }
    }
}