package com.example.moodtrackr.userInterface.permissions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.moodtrackr.R
import androidx.fragment.app.FragmentManager
import com.example.moodtrackr.collectors.workers.util.WorkersUtil
import com.example.moodtrackr.databinding.SuperPermissionsFragmentBinding
import com.example.moodtrackr.userInterface.survey.SurveyFragment
import com.example.moodtrackr.util.PermissionsManager

// Class is unused, only for reference!
class SuperPermissionsFragment  : Fragment(R.layout.super_permissions_fragment) {

    private var _binding: SuperPermissionsFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = SuperPermissionsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        val permsManager = PermissionsManager(this)

        binding.permissionTitleUsage.setOnClickListener{
            val visible = this.binding.root.findViewById<Button>(R.id.appUsageLogsPermBtn).isVisible
            binding.root.findViewById<Button>(R.id.appUsageLogsPermBtn).visibility = if (visible) (View.GONE) else (View.VISIBLE)
        }

        binding.permissionTitleBatteryOpt.setOnClickListener{
            val visible = this.binding.root.findViewById<Button>(R.id.batteryOptPermBtn).isVisible
            binding.root.findViewById<Button>(R.id.batteryOptPermBtn).visibility = if (visible) (View.GONE) else (View.VISIBLE)
        }

        binding.appUsageLogsPermBtn.setOnClickListener{
//            To add multiple permissions, uncomment the following requestMultiplePermissions lines
//            and add the permissions needed!
            if(!permsManager.isUsageAccessGranted()){
                permsManager.grantUsageAccessPermission(this)
            }
            binding.root.findViewById<TextView>(R.id.failureTxtUsage).visibility = View.GONE
            binding.root.findViewById<Button>(R.id.appUsageLogsPermBtn).visibility = View.GONE
            binding.root.findViewById<Button>(R.id.batteryOptPermBtn).visibility = View.VISIBLE
        }

        binding.batteryOptPermBtn.setOnClickListener {
            if(!permsManager.isIgnoringBatteryOptimizations()) {
                permsManager.disableBatteryOptimizations(this)
            }
            binding.root.findViewById<TextView>(R.id.failureTxtBattery).visibility = View.GONE
            binding.root.findViewById<Button>(R.id.batteryOptPermBtn).visibility = View.GONE
            binding.root.findViewById<Button>(R.id.continueBtn).visibility = View.VISIBLE
        }

        binding.continueBtn.setOnClickListener {
            val isUsageGranted = permsManager.isUsageAccessGranted()
            val isBatteryOptDisabled = permsManager.isIgnoringBatteryOptimizations()

            if(!isUsageGranted){
                permsManager.grantUsageAccessPermission(this)
                binding.root.findViewById<TextView>(R.id.failureTxtUsage).visibility = View.VISIBLE
                binding.root.findViewById<Button>(R.id.appUsageLogsPermBtn).visibility = View.VISIBLE
            }
            if(!isBatteryOptDisabled) {
                permsManager.disableBatteryOptimizations(this)
                binding.root.findViewById<TextView>(R.id.failureTxtBattery).visibility = View.VISIBLE
                binding.root.findViewById<Button>(R.id.batteryOptPermBtn).visibility = View.VISIBLE
            }
            if (isBatteryOptDisabled && isBatteryOptDisabled) {
                switchFragment()
            }
        }
        return view;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun switchFragment() {
        try {
            WorkersUtil.queueAll(requireContext().applicationContext)
            val fragment = SurveyFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.fragment_container_view, fragment)
                .commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}