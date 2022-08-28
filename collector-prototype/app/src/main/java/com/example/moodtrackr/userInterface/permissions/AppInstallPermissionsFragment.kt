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
import com.example.moodtrackr.userInterface.survey.SurveyFragment
import com.example.moodtrackr.util.PermissionsManager


class AppInstallPermissionsFragment  : Fragment(R.layout.single_permission_fragment) {

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

        if(permsManager!!.isInstallAppsPermissionGranted()){
            binding.grant.text ="Next"
            isReviewing = true
        }else{
            binding.grant.text ="Permit App Installation Permissions"
        }

        binding.largeHeader.text = "App Installation Permissions"
        binding.explanation.text = "Our app uses this in order to install updates. We will never install malware or malicious code to your phone."
        binding.grant.setOnClickListener{
            if(isReviewing){
                (activity as MainActivity).switchFragment(SurveyFragment());
            }

            if(!permsManager!!.isInstallAppsPermissionGranted()) {
                permsManager!!.grantInstallAppsPermission(this)
            }
        }
        return view;
    }

    override fun onResume() {
        super.onResume()
        if(permsManager!!.isInstallAppsPermissionGranted()){
            if(!isReviewing)
            {
                (activity as MainActivity).switchFragment(SurveyFragment());
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}