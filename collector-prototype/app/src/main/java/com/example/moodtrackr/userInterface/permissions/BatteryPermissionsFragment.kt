package com.example.moodtrackr.userInterface.permissions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moodtrackr.R
import androidx.fragment.app.FragmentManager
import com.example.moodtrackr.MainActivity
import com.example.moodtrackr.collectors.workers.util.WorkersUtil
import com.example.moodtrackr.databinding.SinglePermissionFragmentBinding
import com.example.moodtrackr.util.PermissionsManager


class BatteryPermissionsFragment  : Fragment(R.layout.single_permission_fragment) {

    private var _binding: SinglePermissionFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var permsManager:PermissionsManager? = null;
    private var isReviewing = false; // User is viewing the perms info after already granting it.


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = SinglePermissionFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        permsManager = PermissionsManager(this)

        if(permsManager!!.isIgnoringBatteryOptimizations()){
            binding.grant.text ="Next"
            isReviewing = true
        }else{
            binding.grant.text ="Disable battery optimization"
        }

        binding.largeHeader.text = "Battery";
        binding.explanation.text = "Our app needs battery optimization to be disabled on your device to ensure our application does not stop unexpectedly."+
            "Android is great at keeping your battery topped up, but it can suddenly stop apps even if they use very little power."

        binding.grant.setOnClickListener{
            if(isReviewing){
                (activity as MainActivity).switchFragment(AppUsagePermissionsFragment());
            }
            var isBatteryOptDisabled = permsManager!!.isIgnoringBatteryOptimizations()
            if(!isBatteryOptDisabled) {
                permsManager!!.disableBatteryOptimizations(this)
            }
        }
        return view;
    }

    override fun onResume() {
        super.onResume()
        if(permsManager!!.isIgnoringBatteryOptimizations()){
            if(!isReviewing)
            {
                (activity as MainActivity).switchFragment(AppUsagePermissionsFragment());
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}