package com.example.moodtrackr

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.example.moodtrackr.databinding.ActivityMainBinding
import com.example.moodtrackr.collectors.service.util.NotifUpdateUtil
import com.example.moodtrackr.collectors.workers.util.WorkersUtil
import com.example.moodtrackr.userInterface.demographics.DemoFragment
import com.example.moodtrackr.userInterface.login.LoginFragment
import com.example.moodtrackr.util.DatabaseManager
import com.example.moodtrackr.util.PermissionsManager

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loginStatus = SharedPreferencesStorage(this.applicationContext).retrieveBoolean(
            this.applicationContext.resources.getString(
                R.string.login_status_identifier))
        val setupStatus = SharedPreferencesStorage(this.applicationContext).retrieveBoolean(
            this.applicationContext.resources.getString(
                R.string.setup_status_identifier))

        // inject fragment if it has not been added to the activity
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                //add<SurveyFragment>(R.id.fragment_container_view)
                if (loginStatus != true) add<LoginFragment>(R.id.fragment_container_view)
                if (setupStatus != true) add<DemoFragment>(R.id.fragment_container_view)
                //add<FirstFragment>(R.id.fragment_container_view)
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WorkersUtil.queueServiceMaintainenceOneTime(this.applicationContext)

        val permsManager: PermissionsManager = PermissionsManager(this)
        permsManager.checkAllPermissions()

        val dbManager = DatabaseManager.getInstance(this.applicationContext)

        NotifUpdateUtil.updateNotif(this.applicationContext)

        WorkersUtil.queueServiceMaintenance(this.applicationContext)
        WorkersUtil.queuePeriodic(this.applicationContext)
        WorkersUtil.queueHourly(this.applicationContext)


        binding.fab.setOnClickListener {
//            To add multiple permissions, uncomment the following requestMultiplePermissions lines
//            and add the permissions needed!

            permsManager.checkAllPermissions()
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
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
}