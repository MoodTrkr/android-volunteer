package com.example.moodtrackr

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moodtrackr.collectors.service.DataCollectorService
import com.example.moodtrackr.collectors.service.util.NotifUpdateUtil
import com.example.moodtrackr.collectors.util.CollectionUtil
import com.example.moodtrackr.collectors.workers.util.WorkersUtil
import com.example.moodtrackr.extractors.usage.AppUsageExtractor
import com.example.moodtrackr.extractors.calls.CallLogsStatsExtractor
import com.example.moodtrackr.extractors.geo.GeoDataExtractor
import com.example.moodtrackr.extractors.network.OfflineExtractor
import com.example.moodtrackr.databinding.FragmentFirstBinding
import com.example.moodtrackr.extractors.UnlockCollector
import com.example.moodtrackr.extractors.calls.data.MTCallStats
import com.example.moodtrackr.extractors.usage.data.MTAppUsageLogs
import com.example.moodtrackr.extractors.usage.data.MTAppUsageStats
import com.example.moodtrackr.util.DatabaseManager
import com.example.moodtrackr.util.DatesUtil
import com.example.moodtrackr.util.PermissionsManager
import kotlinx.coroutines.runBlocking


//import com.example.moodtrackr.extractors.usage.AppUsageExtractor

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val permsManager: PermissionsManager = PermissionsManager(this)

        binding.buttonFirst.setOnClickListener {
//            if (savedInstanceState == null) {
//                parentFragmentManager
//                    .beginTransaction()
//                    .add(0, StepsCountExtractor(requireActivity()), "dogList")
//                    .commit()
//            }

            val unlockCollector = UnlockCollector(activity)
            Log.e("DEBUG", unlockCollector.getUnlockCount24h().toString())

//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//            val button: Button = view.findViewById(R.id.button1) as Button
            Log.e("DEBUG", "test_before")
            val usageExtractor = AppUsageExtractor(activity)
            val usageStatsQuery = DatesUtil.yesterdayQueryWrapper( usageExtractor::usageStatsQuery ) as MTAppUsageStats
            val usageEventsQuery = DatesUtil.yesterdayQueryWrapper( usageExtractor::usageEventsQuery ) as MTAppUsageLogs

            Log.e("DEBUG", "test_after")
            Log.e("DEBUG", usageStatsQuery.toString())
            Log.e("DEBUG", usageEventsQuery.toString())

            val callLogsExtractor = CallLogsStatsExtractor(activity)
            val callLogsOutput: MTCallStats = DatesUtil.yesterdayQueryWrapper( callLogsExtractor::queryLogs ) as MTCallStats
            callLogsOutput.calls.forEach{(key, value) -> Log.e("DEBUG", "($key, $value)") }

            val networkExtractor = OfflineExtractor(activity)
            //val networkQuery = networkExtractor.instantReturn()

            val geoExtractor = GeoDataExtractor(activity, permsManager)
            val loc = geoExtractor.getLoc()

            val screenOnTime: Long = DatesUtil.yesterdayQueryWrapper( usageExtractor::screenOnTimeQuery ) as Long
            Log.e("DEBUG", "Screen Time: $screenOnTime")

            val collectionUtil: CollectionUtil = CollectionUtil(activity)
            //collectionUtil.dbInit()
            Log.e("DEBUG", collectionUtil.getAll().toString())
            runBlocking {
                Log.e("DEBUG", "RT_DB")
                Log.e("DEBUG", DatabaseManager.getInstance(requireContext().applicationContext).rtUsageRecordsDAO.getAll().toString())
            }


            NotifUpdateUtil.updateNotif(requireContext().applicationContext)
//
//            WorkersUtil.queuePersistent(requireActivity().applicationContext)
//            WorkersUtil.queuePeriodic(requireActivity().applicationContext)
//            WorkersUtil.queueHourly(requireActivity().applicationContext)
//            WorkersUtil.queueDaily(requireActivity().applicationContext)
            Log.e("DEBUG", "DataCollectorService Vars: ${DataCollectorService.localUnlocks}, ${DataCollectorService.localSteps}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}