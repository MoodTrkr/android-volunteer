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
        if(arguments != null){
            isReviewing = requireArguments().getBoolean("isReviewing")
        }
        _binding = SinglePermissionFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        permsManager = PermissionsManager(this)

        if(isReviewing){
            binding.grant.text ="Next"
        }else{
            binding.grant.text ="Permit usage access"
        }

        binding.largeHeader.text = "App Usage";
        binding.explanation.text = " Our app logs this information to build correlations between mood and the apps people are using."+
                " We also use it to look for a connection between screen time and mood."+
                "You will remain anonymous and your data will never be viewed directly."
        binding.grant.setOnClickListener{
            if(isReviewing){
                val appInstallPermsFragment = AppInstallPermissionsFragment()
                if(isReviewing){
                    val bundle = Bundle()
                    bundle.putBoolean("isReviewing", true)
                    appInstallPermsFragment.arguments = bundle
                }
                (activity as MainActivity).switchFragment(appInstallPermsFragment)
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
                (activity as MainActivity).switchFragment(AppInstallPermissionsFragment());
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}