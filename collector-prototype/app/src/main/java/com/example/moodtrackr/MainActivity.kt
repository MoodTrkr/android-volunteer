package com.example.moodtrackr

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.example.moodtrackr.auth.Auth0Manager
import com.example.moodtrackr.databinding.ActivityMainBinding
import com.example.moodtrackr.collectors.workers.util.WorkersUtil
import com.example.moodtrackr.userInterface.animations.Animations
import com.example.moodtrackr.userInterface.demographics.DemoFragment
import com.example.moodtrackr.userInterface.login.LoginFragment
import com.example.moodtrackr.userInterface.permissions.AppUsagePermissionsFragment
import com.example.moodtrackr.userInterface.permissions.BatteryPermissionsFragment
import com.example.moodtrackr.util.DatabaseManager
import com.example.moodtrackr.util.PermissionsManager
import com.example.moodtrackr.userInterface.permissions.PermissionsFragment
import com.example.moodtrackr.userInterface.survey.SurveyFragment

class MainActivity : AppCompatActivity() {
    private lateinit var permsManager: PermissionsManager
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        permsManager = PermissionsManager(this)
        val loginStatus = SharedPreferencesStorage(this.applicationContext).retrieveBoolean(
            this.applicationContext.resources.getString(
                R.string.login_status_identifier))
        val setupStatus = SharedPreferencesStorage(this.applicationContext).retrieveBoolean(
            this.applicationContext.resources.getString(
                R.string.setup_status_identifier))
        val superPermsGranted = permsManager.isIgnoringBatteryOptimizations() && permsManager.isUsageAccessGranted()

//        permsManager.checkAllPermissions()
        Log.e("DEBUG", "Setup Vars: $loginStatus, $setupStatus ${permsManager.allBasicPermissionsGranted()}")

        redirect(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbManager = DatabaseManager.getInstance(this.applicationContext)

        if (loginStatus == true && setupStatus == true && superPermsGranted) {
            WorkersUtil.queueAll(this.applicationContext)
        }
    }
    override fun onResume() {
        super.onResume()
        if(!permsManager.allBasicPermissionsGranted()){
            switchFragment(PermissionsFragment())
        }else if(!permsManager.isIgnoringBatteryOptimizations()){
            switchFragment(BatteryPermissionsFragment())
        }else if(!permsManager.isUsageAccessGranted()){
            switchFragment(AppUsagePermissionsFragment())
        }
    }
    fun redirect(savedInstanceState: Bundle?){
        val loginStatus = SharedPreferencesStorage(this.applicationContext).retrieveBoolean(
            this.applicationContext.resources.getString(
                R.string.login_status_identifier))
        val setupStatus = SharedPreferencesStorage(this.applicationContext).retrieveBoolean(
            this.applicationContext.resources.getString(
                R.string.setup_status_identifier))
        val batteryPermsGranted = permsManager.isIgnoringBatteryOptimizations()
        val usagePermsGranted = permsManager.isUsageAccessGranted()

        val enableDebugging = false
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            //add<SurveyFragment>(R.id.fragment_container_view)
            when {
                loginStatus != true -> add<LoginFragment>(R.id.fragment_container_view)
                setupStatus != true -> add<DemoFragment>(R.id.fragment_container_view)
                !permsManager.allBasicPermissionsGranted() -> add<PermissionsFragment>(R.id.fragment_container_view)
                !batteryPermsGranted -> add<BatteryPermissionsFragment>(R.id.fragment_container_view)
                !usagePermsGranted -> add<AppUsagePermissionsFragment>(R.id.fragment_container_view)
                enableDebugging -> add<FirstFragment>(R.id.fragment_container_view)
                savedInstanceState == null -> add<SurveyFragment>(R.id.fragment_container_view)
            }
        }
    }
    fun guardedRedirect(savedInstanceState: Bundle?){
        val loginStatus = SharedPreferencesStorage(this.applicationContext).retrieveBoolean(
            this.applicationContext.resources.getString(
                R.string.login_status_identifier))
        val setupStatus = SharedPreferencesStorage(this.applicationContext).retrieveBoolean(
            this.applicationContext.resources.getString(
                R.string.setup_status_identifier))
        val batteryPermsGranted = permsManager.isIgnoringBatteryOptimizations()
        val usagePermsGranted = permsManager.isUsageAccessGranted()

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            when {
                loginStatus != true -> switchFragment(LoginFragment())
                setupStatus != true -> switchFragment(DemoFragment())
                !permsManager.allBasicPermissionsGranted() -> switchFragment(PermissionsFragment())
                !batteryPermsGranted -> switchFragment(BatteryPermissionsFragment())
                !usagePermsGranted -> switchFragment(AppUsagePermissionsFragment())
                savedInstanceState == null -> switchFragment(SurveyFragment())
            }
        }
        Log.e("DEBUG", "Switching fragment...")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun switchFragment(fragment:Fragment) {
        binding.fragmentContainerView.apply {
            alpha = 1f
            visibility = View.VISIBLE

            animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        try {
                            val fragmentManager: FragmentManager = supportFragmentManager
                            fragmentManager.beginTransaction().replace(R.id.fragment_container_view, fragment)
                                .commit()
                            Animations.fadeIn(400,binding.fragmentContainerView)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        }
    }

    fun scrollToTop(){
        binding.mainScrollView.scrollTo(0, 0)
    }

    fun showPopup(v: View) {
        val popup = PopupMenu(this, v)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when(item.itemId) {
//                R.id.devButton ->
//                    switchFragment(FirstFragment())
                R.id.permissionsButton ->
                    switchFragment(PermissionsFragment())
                R.id.logoutButton ->{
                    Auth0Manager(this).logout()
                    switchFragment(LoginFragment())
                }
                R.id.demographicsButton ->
                    switchFragment(DemoFragment())
            }
            true
        });
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.navigation_popup, popup.menu)
        popup.show()
    }

    companion object {
        val SURVEY_NOTIF_CLICKED = "survey_notif"
    }
}
