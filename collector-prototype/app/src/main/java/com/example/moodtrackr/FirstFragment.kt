package com.example.moodtrackr

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moodtrackr.databinding.FragmentFirstBinding


//import com.example.moodtrackr.AppUsageExtractor

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
//            geoExtractor.getLoc()                                               //this is async, so we'll print here instead of returning anything
//            val locStr: String = "Latitude: " + loc.latitude.toString() + ", Longitude: " + loc.longitude.toString() + ", Accuracy: " + loc.accuracy.toString()
//            Log.e("DEBUG", loc.toString())

            //Log.e("DEBUG", networkQuery.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}