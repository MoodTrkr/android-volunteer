package com.example.moodtrackr.userInterface.demographics
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.moodtrackr.MainActivity
import com.example.moodtrackr.R
import com.example.moodtrackr.R.id.*
import com.example.moodtrackr.auth.Auth0Manager
import com.example.moodtrackr.databinding.DemoFragmentBinding
import com.example.moodtrackr.extractors.geo.GeoDataExtractor
import com.example.moodtrackr.util.DatesUtil
import com.example.moodtrackr.userInterface.permissions.PermissionsFragment
import com.example.moodtrackr.userInterface.survey.SurveyFragment
import com.example.moodtrackr.util.PermissionsManager
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import okhttp3.internal.toImmutableMap
import java.util.*

class DemoFragment(): Fragment(R.layout.demo_fragment) {
    private lateinit var auth0Manager: Auth0Manager
    private lateinit var geoDataExtractor: GeoDataExtractor
    private lateinit var permissionsManager: PermissionsManager

    private var country: String? = null
    private var cal: Calendar = Calendar.getInstance()
    private lateinit var race: String
    private lateinit var gender: String

    private var _binding: DemoFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DemoFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        this.permissionsManager = PermissionsManager(this)
        this.geoDataExtractor = GeoDataExtractor(requireActivity(), permissionsManager)
        geoDataExtractor.setCountry()
        Log.d("DEBUG", "Country: $country")

        this.auth0Manager = Auth0Manager(requireActivity())

        binding.dobDatePicker.setOnDateChangedListener{ _, year, month, day ->
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(year, month, day)
            cal = calendar
        }

        binding.root.findViewById<Spinner>(genderDropdown).onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, i: Int, id: Long) {
                gender = parent?.getItemAtPosition(i) as String
            }
        }

        binding.root.findViewById<Spinner>(raceDropdown).onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, i: Int, id: Long) {
                race = parent?.getItemAtPosition(i) as String
            }
        }

        binding.dobNextBtn.setOnClickListener { _ ->
            binding.root.findViewById<DatePicker>(dobDatePicker).visibility = View.GONE
            binding.root.findViewById<Button>(dobNextBtn).visibility = View.GONE
            binding.root.findViewById<Spinner>(genderDropdown).visibility = View.VISIBLE
            binding.root.findViewById<Button>(genderNextBtn).visibility = View.VISIBLE
        }
        binding.genderNextBtn.setOnClickListener { _ ->
            binding.root.findViewById<Spinner>(genderDropdown).visibility = View.GONE
            binding.root.findViewById<Button>(genderNextBtn).visibility = View.GONE
            binding.root.findViewById<Spinner>(raceDropdown).visibility = View.VISIBLE
            binding.root.findViewById<Button>(raceNextBtn).visibility = View.VISIBLE
        }
        binding.raceNextBtn.setOnClickListener { _ ->
            binding.root.findViewById<Spinner>(raceDropdown).visibility = View.GONE
            binding.root.findViewById<Button>(raceNextBtn).visibility = View.GONE
            binding.root.findViewById<Button>(continueBtn).visibility = View.VISIBLE
        }

        binding.dobPrompt.setOnClickListener {
            val visible = binding.root.findViewById<DatePicker>(dobDatePicker).isVisible
            binding.root.findViewById<DatePicker>(dobDatePicker).visibility = if (visible) (View.GONE) else (View.VISIBLE)
        }

        binding.genderPrompt.setOnClickListener {
            val visible = binding.root.findViewById<Spinner>(genderDropdown).isVisible
            binding.root.findViewById<Spinner>(genderDropdown).visibility = if (visible) (View.GONE) else (View.VISIBLE)
        }

        binding.racePrompt.setOnClickListener {
            val visible = binding.root.findViewById<Spinner>(raceDropdown).isVisible
            binding.root.findViewById<Spinner>(raceDropdown).visibility = if (visible) (View.GONE) else (View.VISIBLE)
        }

        binding.continueBtn.setOnClickListener {
            val job = runBlocking {
                handleComplete()
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

    private fun handleComplete(): CompletableDeferred<Boolean> {
        var deferred = CompletableDeferred<Boolean>()

        country = geoDataExtractor.getCountry()
        Log.d("DEBUG", "Config: $country, $race, $gender, ${cal?.time}")
        var metadata = mutableMapOf<String, String>()

        metadata["RACE"] = race
        metadata["GENDER"] = gender
        if (country != null) metadata["COUNTRY"] = country!! else metadata["COUNTRY"] = "NULL"
        metadata["DOB"] = cal.let { it -> DatesUtil.truncateDate(it.time) }.time.toString()
         Auth0Manager.updateUserMetadata(requireContext().applicationContext, metadata.toImmutableMap())
        deferred.complete(true)

        val job = Auth0Manager.updateUserMetadataAsync(requireContext().applicationContext, metadata.toImmutableMap())
        job.invokeOnCompletion{
            deferred.complete(true)
        }

        return deferred
    }

    private fun switchFragment() {
        try {
            (activity as MainActivity).guardedRedirect(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}