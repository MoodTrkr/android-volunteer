package com.example.moodtrackr.userInterface.login
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moodtrackr.R
import com.example.moodtrackr.auth.Auth0Manager
import com.example.moodtrackr.databinding.LoginFragmentBinding

class LoginFragment:Fragment(R.layout.login_fragment) {
    private lateinit var auth0Manager: Auth0Manager

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
        binding.loginBtn.setOnClickListener { _ -> auth0Manager.loginWithBrowser()}
//        binding.logoutBtn.setOnClickListener { _ -> auth0Manager.logout()}
//        binding.userProfileBtn.setOnClickListener { _ -> auth0Manager.showUserProfile()}
        return view;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}