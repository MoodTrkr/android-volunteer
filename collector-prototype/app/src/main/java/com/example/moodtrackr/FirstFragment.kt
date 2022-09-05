package com.example.moodtrackr

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.example.moodtrackr.auth.Auth0Manager
import com.example.moodtrackr.collectors.service.DataCollectorService
import com.example.moodtrackr.collectors.service.util.NotifUpdateUtil
import com.example.moodtrackr.collectors.util.CollectionUtil
import com.example.moodtrackr.collectors.workers.util.WorkersUtil
import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.databinding.FragmentFirstBinding
import com.example.moodtrackr.extractors.UnlockCollector
import com.example.moodtrackr.extractors.calls.CallLogsStatsExtractor
import com.example.moodtrackr.extractors.calls.data.MTCallStats
import com.example.moodtrackr.extractors.network.OfflineExtractor
import com.example.moodtrackr.extractors.usage.AppUsageExtractor
import com.example.moodtrackr.extractors.usage.data.MTAppUsageLogs
import com.example.moodtrackr.extractors.usage.data.MTAppUsageStats
import com.example.moodtrackr.router.RestClient
import com.example.moodtrackr.router.data.MTUsageDataStamped
import com.example.moodtrackr.sleepextractor.SleepExtractor
import com.example.moodtrackr.userInterface.survey.SurveyFragment
import com.example.moodtrackr.util.DatabaseManager
import com.example.moodtrackr.util.DatesUtil
import com.example.moodtrackr.util.PermissionsManager
import com.example.moodtrackr.util.UpdateManager
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileWriter


//import com.example.moodtrackr.extractors.usage.AppUsageExtractor

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(R.layout.fragment_first) {
    private lateinit var auth0Manager: Auth0Manager
    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        this.auth0Manager = Auth0Manager(requireActivity())
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val permsManager: PermissionsManager = PermissionsManager(this)
        //permsManager.checkAllPermissions()

        binding.buttonFirst.setOnClickListener {
//            if (savedInstanceState == null) {
//                parentFragmentManager
//                    .beginTransaction()
//                    .add(0, StepsCountExtractor(requireActivity()), "dogList")
//                    .commit()
//            }

            val unlockCollector = UnlockCollector(activity)
            Log.d("DEBUG", unlockCollector.getUnlockCount24h().toString())

//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//            val button: Button = view.findViewById(R.id.button1) as Button
            Log.d("DEBUG", "test_before")
            val usageExtractor = AppUsageExtractor(activity)
            val usageStatsQuery = DatesUtil.yesterdayQueryWrapper( usageExtractor::usageStatsQuery ) as MTAppUsageStats
            val usageEventsQuery = DatesUtil.yesterdayQueryWrapper( usageExtractor::usageEventsQuery ) as MTAppUsageLogs

            Log.d("DEBUG", "test_after")
            Log.d("DEBUG", usageStatsQuery.toString())
            Log.d("DEBUG", usageEventsQuery.toString())

            val callLogsExtractor = CallLogsStatsExtractor(activity)
            val callLogsOutput: MTCallStats = DatesUtil.yesterdayQueryWrapper( callLogsExtractor::queryLogs ) as MTCallStats
            callLogsOutput.calls.forEach{(key, value) -> Log.d("DEBUG", "($key, $value)") }

            val networkExtractor = OfflineExtractor(activity)
            //val networkQuery = networkExtractor.instantReturn()

            val screenOnTime: Long = DatesUtil.yesterdayQueryWrapper( usageExtractor::screenOnTimeQuery ) as Long
            Log.d("DEBUG", "Screen Time: $screenOnTime")

            val collectionUtil: CollectionUtil = CollectionUtil(activity)
            //collectionUtil.dbInit()
            Log.d("DEBUG", collectionUtil.getAll().toString())
            runBlocking {
                Log.d("DEBUG", "RT_DB")
                Log.d("DEBUG", DatabaseManager.getInstance(requireContext().applicationContext).rtUsageRecordsDAO.getAll().toString())
            }

            NotifUpdateUtil.updateNotif(requireContext().applicationContext)
//
//            WorkersUtil.queuePersistent(requireActivity().applicationContext)
//            WorkersUtil.queuePeriodic(requireActivity().applicationContext)
//            WorkersUtil.queueHourly(requireActivity().applicationContext)
//            WorkersUtil.queueDaily(requireActivity().applicationContext)
            Log.d("DEBUG", "DataCollectorService Vars: ${DataCollectorService.localUnlocks}, ${DataCollectorService.localSteps}")


            var yesterday = DatesUtil.getYesterdayTruncated()
            Log.d("DEBUG", "Yesterday: $yesterday")

            yesterday = DatesUtil.truncateDate(yesterday)
            Log.d("DEBUG", "Yesterday Truncated: $yesterday")
            Log.d("DEBUG", "Yesterday Bounds: ${DatesUtil.getDayBounds(yesterday)}")

//            val record: MTUsageData = DBHelper.getObjSafe(requireContext().applicationContext, yesterday)
//            Log.d("DEBUG", "Yesterday Obj: $record")
//            Log.d("DEBUG", "Yesterday Daily Complete: ${record.dailyCollection.complete}")

            val myFile = File(requireContext().applicationContext.filesDir, "output.json")
            if (myFile.exists())                    myFile.delete()
            if (!myFile.parentFile.exists())        myFile.parentFile.mkdirs()
            Log.e("DEBUG", myFile.absolutePath)
            myFile.createNewFile()

            val gson = GsonBuilder().setPrettyPrinting().create()
            //val writer = Files.newBufferedWriter(myFile.toPath())
            val writer = FileWriter(myFile)
            runBlocking {
                gson.toJson(DatabaseManager.getInstance(requireContext().applicationContext).usageRecordsDAO.getAll(), writer)
            }
            writer.flush()
            writer.close()
        }

        binding.loginBtnFirstFragment.setOnClickListener { _ -> auth0Manager.loginWithBrowser()}
        binding.logoutBtnFirstFragment.setOnClickListener { _ -> auth0Manager.logout()}
        binding.pushUsageObjBtn.setOnClickListener {
            val restClient = RestClient.getInstance(requireContext().applicationContext)
            var usage: MTUsageData?
            runBlocking {
                usage = DatabaseManager.getInstance(requireContext().applicationContext).usageRecordsDAO.getObjOnDay(
                    DatesUtil.getYesterdayTruncated().time
                )
            }
            Log.e("DEBUG", "Pushing to Server!")
            runBlocking {
                usage?.let {
                    val stamped = MTUsageDataStamped.stampUsageData(
                        requireContext().applicationContext, it)
                    RestClient.safeApiCall(
                        requireContext().applicationContext,
                        Dispatchers.Default,
                        restClient::insertUsageData,
                        DatesUtil.getYesterdayTruncated().time,
                        stamped
                    )}
            }
        }

        binding.sleepBoundsBtn.setOnClickListener {
            val job = SleepExtractor.computeSleepBoundsAsync(1, requireContext().applicationContext)
            job.invokeOnCompletion {
                Log.e("MDTKR_SLEEP_EXT", job.getCompleted().toString())
            }
        }

        binding.checkForUpdatesBtn.setOnClickListener {
            UpdateManager.checkForUpdates(requireContext())
        }

        binding.refreshUserBtn.setOnClickListener { auth0Manager.refreshCredentials() }
        binding.metadataBtn.setOnClickListener {
            auth0Manager.getUserMetadata()
            val metadata = SharedPreferencesStorage(requireContext().applicationContext).retrieveString(
                requireContext().applicationContext.resources.getString(
                    R.string.auth0_user_metadata))
            Log.e("DEBUG", "$metadata")

        }

        binding.popReqBtn.setOnClickListener {
            WorkersUtil.queueRouterRequestsWorkerOneTime(requireContext().applicationContext)
        }

        binding.surveyBtn.setOnClickListener {
            switchFragment(SurveyFragment());
        }

        binding.installUpdatesBtn.setOnClickListener {
            UpdateManager.checkUpdatesDownloaded(requireContext().applicationContext)
        }
    }

    private fun switchFragment(fragment:Fragment) {
        try {
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.fragment_container_view, fragment)
                .commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}