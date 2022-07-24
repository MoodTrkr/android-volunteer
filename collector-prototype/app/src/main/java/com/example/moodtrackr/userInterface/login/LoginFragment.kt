package com.example.moodtrackr.userInterface.login
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.example.moodtrackr.FirstFragment
import com.example.moodtrackr.MainActivity
import com.example.moodtrackr.R
import com.example.moodtrackr.auth.Auth0Manager
import com.example.moodtrackr.databinding.LoginFragmentBinding
import com.example.moodtrackr.userInterface.demographics.DemoFragment
import com.example.moodtrackr.util.PermissionsManager
import kotlinx.coroutines.runBlocking


class LoginFragment(): Fragment(R.layout.login_fragment) {
    private lateinit var auth0Manager: Auth0Manager
    private val permissionsManager = MainActivity.permsManager

    private var _binding: LoginFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        this.auth0Manager = Auth0Manager(requireActivity())
        binding.loginBtn.setOnClickListener {
            val job = runBlocking {
                auth0Manager.loginAsync()
            }
            job.invokeOnCompletion {
                switchFragment()
            }
        }
//        binding.logoutBtn.setOnClickListener { _ -> auth0Manager.logout()}
//        binding.userProfileBtn.setOnClickListener { _ -> auth0Manager.showUserProfile()}
        return view;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun switchFragment() {
        try {
            val setupStatus = SharedPreferencesStorage(requireContext().applicationContext).retrieveBoolean(
                requireContext().applicationContext.resources.getString(
                    R.string.setup_status_identifier))
            Log.e("DEBUG", "SETUP STATUS: $setupStatus")
            var fragment: Fragment = if (setupStatus == true) {
                FirstFragment()
            } else {
                DemoFragment()
            }
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.fragment_container_view, fragment)
                .commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}