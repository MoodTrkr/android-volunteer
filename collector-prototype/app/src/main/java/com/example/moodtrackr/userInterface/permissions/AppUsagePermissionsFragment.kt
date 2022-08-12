package com.example.moodtrackr.userInterface.permissions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moodtrackr.R
import androidx.fragment.app.FragmentManager
import com.example.moodtrackr.collectors.workers.util.WorkersUtil
import com.example.moodtrackr.databinding.SinglePermissionFragmentBinding
import com.example.moodtrackr.userInterface.survey.SurveyFragment
import com.example.moodtrackr.util.PermissionsManager


class AppUsagePermissionsFragment  : Fragment(R.layout.single_permission_fragment) {

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

        if(permsManager!!.isUsageAccessGranted()){
            binding.grant.text ="Next"
            isReviewing = true
        }else{
            binding.grant.text ="Permit usage access"
        }

        binding.largeHeader.text = "App Usage";
        binding.explanation.text = "Um dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id es";

        binding.grant.setOnClickListener{
            if(isReviewing){
                switchFragment()
            }

            if(!permsManager!!.isUsageAccessGranted()) {
                permsManager!!.grantUsageAccessPermission(this)
            }
        }
        return view;
    }

    override fun onResume() {
        super.onResume()
        if(permsManager!!.isUsageAccessGranted()){
            if(!isReviewing)
            {
                switchFragment()
            }
        }
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