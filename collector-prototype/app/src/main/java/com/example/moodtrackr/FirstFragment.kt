package com.example.moodtrackr

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moodtrackr.extractors.AppUsageExtractor
import com.example.moodtrackr.extractors.CallLogsStatsExtractor
import com.example.moodtrackr.extractors.geo.GeoDataExtractor
import com.example.moodtrackr.extractors.network.OfflineExtractor
import com.example.moodtrackr.databinding.FragmentFirstBinding
import com.example.moodtrackr.extractors.StepsCountExtractor
import com.example.moodtrackr.extractors.UnlockCollector
import com.example.moodtrackr.utilities.PermissionsManager
import kotlin.concurrent.thread


//import com.example.moodtrackr.extractors.AppUsageExtractor

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
            if (savedInstanceState == null) {
                parentFragmentManager
                    .beginTransaction()
                    .add(0, StepsCountExtractor(requireActivity()), "dogList")
                    .commit()
            }

            thread(start = true) {
                val unlockCollector = UnlockCollector(activity)
                Log.e("DEBUG", unlockCollector.getUnlockCount24h().toString())
            }

//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//            val button: Button = view.findViewById(R.id.button1) as Button
            Log.e("DEBUG", "test_before")
            val usageExtractor = AppUsageExtractor(activity)
            val usageQuery = usageExtractor.instantReturn()
//            button.text = "d"
            Log.e("DEBUG", "test_after")
            Log.e("DEBUG", usageQuery.toString())

            val callLogsExtractor = CallLogsStatsExtractor(activity)
            val callLogsOutput = callLogsExtractor.instantReturn()
            for (line in callLogsOutput) {
                Log.e("DEBUG", line)
            }

            val networkExtractor = OfflineExtractor(activity)
            //val networkQuery = networkExtractor.instantReturn()

            val geoExtractor = GeoDataExtractor(activity, permsManager)
            val loc = geoExtractor.getLoc()

            val screenOnTime: Long = usageExtractor.screenOnTimeQuery()
            Log.e("DEBUG", "Screen Time: $screenOnTime")

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}