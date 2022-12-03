package com.iulu.timer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.children
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.ChipGroup
import com.iulu.timer.databinding.ActionBottomSheetBinding
import com.iulu.timer.timer.TimerPref

class ActionsBottomSheet : BottomSheetDialogFragment() {
    private var _binding: ActionBottomSheetBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ActionBottomSheetBinding.inflate(inflater, container, false).run {
        _binding = this
        binding.root
    }

    @DrawableRes
    var checkedChipIconId: Int? = null
    private set

    override fun onStart() {
        super.onStart()
        binding.chipGroup.apply {
            restoreCheckedChip()
            setOnCheckedStateChangeListener { _, _ ->
                saveCheckedChip()
            }
        }
    }

    private fun ChipGroup.restoreCheckedChip() {
        val savedAction = TimerPref.getActionClass(context)
        children.forEach { it as TimerChip
            if (it.tag as String == savedAction) {
                it.isChecked = true
            }
        }
    }

    private fun ChipGroup.saveCheckedChip() {
        children.forEach { it as TimerChip
            if (it.isChecked) {
                checkedChipIconId = it.chipIconRes
                TimerPref.saveActionClass(requireContext(), it.tag as String)
                TimerPref.saveCheckedIcon(requireContext(), checkedChipIconId!!)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}