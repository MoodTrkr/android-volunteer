package com.example.moodtrackr.userInterface.permissions

import android.widget.TextView
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.moodtrackr.R
import com.example.moodtrackr.databinding.PermissionsFragmentBinding
import com.example.moodtrackr.MainActivity
import com.example.moodtrackr.userInterface.animations.Animations
import com.example.moodtrackr.util.PermissionsManager


class PermissionsFragment  : Fragment(R.layout.permissions_fragment) {

    private var _binding: PermissionsFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val collapsablePermissions: MutableList<LinearLayout> = ArrayList();
    private var permsManager:PermissionsManager? = null;
    private var isReviewing = false; // User is viewing the perms info after already granting it.


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = PermissionsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        var permissions = arrayOf(
            PermissionDO("Call logs", "We look at call logs to measure social interaction through the number of " +
                    "incoming and outgoing calls. Information about what is " +
                    "being said is never recorded "),
            PermissionDO("Location","We only use location a single time for demographic information on account setup."),
            PermissionDO("Physical Activity","Our app logs your daily steps to get a feel for how physically active you are."),
        )

        permsManager = PermissionsManager(this)
        binding.getPermissions.setOnClickListener{
//            To add multiple permissions, uncomment the following requestMultiplePermissions lines
//            and add the permissions needed!
            if(permsManager!!.allBasicPermissionsGranted()){
                (activity as MainActivity).switchFragment(BatteryPermissionsFragment());
            }else{
                permsManager!!.checkAllBasicPermissions()
            }

        }

        if(permsManager!!.allBasicPermissionsGranted()){
            binding.getPermissions.text = "Next"
            isReviewing = true
        }else{
            binding.getPermissions.text = "Grant Permissions"
        }


        for (permission in permissions){
            val rootContainer: LinearLayout = LinearLayout(activity);
            rootContainer.setLayoutParams(ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT))
            rootContainer.orientation = LinearLayout.VERTICAL;
            rootContainer.setGravity(Gravity.CENTER);

            val dropDownContainer: LinearLayout = LinearLayout(activity);
            dropDownContainer.orientation = LinearLayout.VERTICAL;
            dropDownContainer.setLayoutParams(ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT))
            val scale = resources.displayMetrics.density
            val dpAsPixels = (30 * scale + 0.5f).toInt();
            dropDownContainer.setPadding(dpAsPixels,dpAsPixels,dpAsPixels,dpAsPixels);
            dropDownContainer.setVisibility(View.GONE);
            collapsablePermissions.add(dropDownContainer);


            val permButton: TextView = TextView(activity)
            permButton.setTextAppearance(R.style.clickableTextDark)
            permButton.setText(permission.name + " permission");
            permButton.setGravity(Gravity.CENTER)
            permButton.setLayoutParams(ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT))
            permButton.setBackgroundResource(R.drawable.bordered_box);
            permButton.setPadding(dpAsPixels,dpAsPixels,dpAsPixels,dpAsPixels);
            permButton.setOnClickListener {
                if( dropDownContainer.visibility == View.VISIBLE){
                    Animations.collapse(dropDownContainer)
                }else{
                    for(dropDown in collapsablePermissions){
                        Animations.collapse(dropDown)
                    }
                    Animations.expand(dropDownContainer)
                }
            };


            val topic: TextView =TextView(activity);
            topic.setTextAppearance(R.style.subHeader)
            topic.setText("Permissions")


            val permissionName: TextView =TextView(activity);
            permissionName.setTextAppearance(R.style.largeHeader)
            permissionName.setText(permission.name)


            val question: TextView =TextView(activity);
            question.setTextAppearance(R.style.regularHeader)
            question.setText("Why do we need this permission?")


            val explanation: TextView =TextView(activity);
            explanation.setPadding(0,10,0,0)
            explanation.setTextAppearance(R.style.darkText)
            explanation.setText(permission.explanation)


            rootContainer.addView(permButton);
            rootContainer.addView(dropDownContainer);
            dropDownContainer.addView(topic)
            dropDownContainer.addView(permissionName)
            dropDownContainer.addView(question)
            dropDownContainer.addView(explanation)
            binding.container.addView(rootContainer)


        }
        return view;
    }

    override fun onResume() {
        super.onResume()
        if(permsManager!!.allBasicPermissionsGranted()){
            if(!isReviewing)
            {
                (activity as MainActivity).switchFragment(BatteryPermissionsFragment());
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}