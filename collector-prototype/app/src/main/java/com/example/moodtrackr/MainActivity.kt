package com.example.moodtrackr

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.example.moodtrackr.databinding.ActivityMainBinding
import com.example.moodtrackr.collectors.service.DataCollectorService
import com.example.moodtrackr.collectors.service.util.NotifUpdateUtil
import com.example.moodtrackr.collectors.workers.UnlocksWorker
import com.example.moodtrackr.collectors.workers.notif.SurveyNotifBuilder
import com.example.moodtrackr.collectors.workers.util.WorkersUtil
import com.example.moodtrackr.userInterface.demographics.DemoFragment
import com.example.moodtrackr.userInterface.login.LoginFragment
import com.example.moodtrackr.util.DatabaseManager
import com.example.moodtrackr.util.PermissionsManager
import com.example.moodtrackr.userInterface.permissions.PermissionsFragment
import com.example.moodtrackr.userInterface.permissions.SuperPermissionsFragment
import com.example.moodtrackr.userInterface.survey.SurveyFragment

class MainActivity : AppCompatActivity() {
    private lateinit var permsManager: PermissionsManager
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
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
        Log.e("DEBUG", "Setup Vars: $loginStatus, $setupStatus ${permsManager.allPermissionsGranted()}")

        // inject fragment if it has not been added to the activity
        when {
            (savedInstanceState == null) -> {
                val enableDebugging = true
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    //add<SurveyFragment>(R.id.fragment_container_view)
                    when {
                        loginStatus != true -> add<LoginFragment>(R.id.fragment_container_view)
                        setupStatus != true -> add<DemoFragment>(R.id.fragment_container_view)
                        !permsManager.allPermissionsGranted() -> add<PermissionsFragment>(R.id.fragment_container_view)
                        !superPermsGranted -> add<SuperPermissionsFragment>(R.id.fragment_container_view)
                        enableDebugging -> add<FirstFragment>(R.id.fragment_container_view)
                        permsManager.allPermissionsGranted() -> add<SurveyFragment>(R.id.fragment_container_view)
                        }
                }
            }
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbManager = DatabaseManager.getInstance(this.applicationContext)

        if (loginStatus == true && setupStatus == true && superPermsGranted) {
            WorkersUtil.queueAll(this.applicationContext)
        }

        binding.fab.setOnClickListener {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                switchFragment(FirstFragment())
            }
        }
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

    private fun switchFragment(fragment:Fragment) {
        try {
            val fragmentManager: FragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.fragment_container_view, fragment)
                .commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        val SURVEY_NOTIF_CLICKED = "survey_notif"
    }
}