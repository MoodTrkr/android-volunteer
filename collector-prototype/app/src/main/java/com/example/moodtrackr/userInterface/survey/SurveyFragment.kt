package com.example.moodtrackr.userInterface.survey
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.moodtrackr.FirstFragment
import com.example.moodtrackr.MainActivity
import com.example.moodtrackr.R
import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.databinding.SurveyFragmentBinding
import com.example.moodtrackr.db.records.UsageRecordsDAO
import com.example.moodtrackr.extractors.sleep.data.MTSleepData
import com.example.moodtrackr.router.RestClient
import com.example.moodtrackr.sleepextractor.SleepExtractor
import com.example.moodtrackr.util.DatabaseManager
import com.example.moodtrackr.util.DatesUtil
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import java.sql.Date
import java.time.*
import java.util.*
import kotlin.math.ceil
import kotlin.random.Random

class SurveyFragment  : Fragment(R.layout.survey_fragment) {

    private var _binding: SurveyFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var surveyDO: SurveyDO;
    private lateinit var usageRecordsDao: UsageRecordsDAO
    private var usageRecord: MTUsageData? = null
    private var surveyComplete: Boolean? = false;

    private var completeQuestionNumber: Int = 0;
    private var surveyQuestionNumber: Int = 0;

    private val imageIds = arrayOf(R.drawable.cute_animal_0,R.drawable.cute_animal_1,
        R.drawable.cute_animal_2, R.drawable.cute_animal_3 )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = SurveyFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        //begin computing sleep time bounds
        sleepBoundsCompute()

        // Change this when we have sleep data
        val currentSurveyDate = LocalDateTime.now().minusDays(1)
        var convertedDate = Date.from(currentSurveyDate.atZone(ZoneId.systemDefault()).toInstant());
        convertedDate = DatesUtil.truncateDate(convertedDate);

        usageRecordsDao = DatabaseManager.getInstance(requireActivity().applicationContext).usageRecordsDAO;
        runBlocking {
            usageRecord = usageRecordsDao.getObjOnDay(convertedDate.time)
        }

        surveyComplete = usageRecord?.surveyData?.complete
        surveyDO = SurveyDO();
        if( surveyComplete == true){
            // survey is complete!
            showSurveyComplete()
        }else {
            setQuestion();
        }
        completeQuestionNumber = surveyDO.questionDOS.size + 1
        surveyQuestionNumber = surveyDO.questionDOS.size

        binding.back.setOnClickListener {
            if(surveyComplete == true){
                surveyDO = SurveyDO()
                surveyComplete = false
            }
            else{
                surveyDO.currentQuestionNumber -= 1;
            }
            setQuestion();
        };
        binding.optionOne.setOnClickListener { v ->
            handleOptionClick(v);
        };
        binding.optionTwo.setOnClickListener { v ->
            handleOptionClick(v);
        };
        binding.optionThree.setOnClickListener { v ->
            handleOptionClick(v);
        };
        binding.optionFour.setOnClickListener { v ->
            handleOptionClick(v);
        };
        binding.optionFive.setOnClickListener { v ->
            handleOptionClick(v);
        };
        binding.optionSix.setOnClickListener { v ->
            handleOptionClick(v);
        };
        binding.complete.setOnClickListener {
            // We are assuming wakeups are all on the same day
            // and sleep times are on the previous day if pm, the next day if am
            var sleepTime = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(binding.sleepTime.hour,binding.sleepTime.minute));
            var wakeupTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(binding.wakeUpTime.hour,binding.wakeUpTime.minute));
            surveyDO.sleepData = MTSleepData( sleepTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                wakeupTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            surveyDO.currentQuestionNumber += 1;
            setQuestion();
            // sleep data is one hour ahead at the moment...
        };
        binding.optionOne.setTag(R.string.buttonIdForTag, 0);
        binding.optionTwo.setTag(R.string.buttonIdForTag, 1);
        binding.optionThree.setTag(R.string.buttonIdForTag, 2);
        binding.optionFour.setTag(R.string.buttonIdForTag, 3);
        binding.optionFive.setTag(R.string.buttonIdForTag, 4);
        binding.optionSix.setTag(R.string.buttonIdForTag, 5);

        return view;
    }
    private fun clearCard(){
        binding.sleepData.visibility = View.GONE;
        binding.meme.visibility = View.GONE;
        binding.options.visibility = View.GONE;
        binding.back.visibility = View.GONE;
    }
    private fun setQuestion(){
        if(surveyDO.currentQuestionNumber == surveyQuestionNumber ){
            // Questions finished, need sleep time
            binding.back.text = "Previous question"
            binding.prompt.text = "Sleep Quality";
            clearCard();
            binding.sleepData.visibility = View.VISIBLE;
            binding.back.visibility = View.VISIBLE;
        }
        else if (surveyDO.currentQuestionNumber == completeQuestionNumber){
            // survey is finished

            binding.loading.visibility= View.VISIBLE;
            var surveyData =  surveyDO.getSurveyData();
            surveyComplete = true;

            CoroutineScope(Dispatchers.IO).launch {
                val appContext: Context = requireContext().applicationContext
                usageRecord?.complete = true
                usageRecord?.surveyData = surveyData
                usageRecordsDao.update(usageRecord!!)
                RestClient.safeApiCall(
                    appContext,
                    Dispatchers.IO,
                    RestClient.getInstance(appContext)::insertUsageData,
                    DatesUtil.getYesterdayTruncated().time,
                    usageRecord!!
                )
            }
            binding.loading.visibility= View.GONE;
            showSurveyComplete();
        }
        else{
            binding.back.text = "Previous question"
            binding.meme.visibility = View.GONE;
            binding.sleepData.visibility = View.GONE;
            binding.options.visibility = View.VISIBLE;
            binding.back.visibility = View.VISIBLE;
            val currentQuestion = surveyDO.getCurrentQuestion();
            binding.optionOne.text = currentQuestion.optionDOS[0].text;
            binding.optionTwo.text = currentQuestion.optionDOS[1].text;
            binding.optionThree.text = currentQuestion.optionDOS[2].text;
            binding.optionFour.text = currentQuestion.optionDOS[3].text;
            binding.optionFive.text = currentQuestion.optionDOS[4].text;
            binding.optionSix.text = currentQuestion.optionDOS[5].text;

            binding.prompt.text = currentQuestion.prompt;

            if(surveyDO.currentQuestionNumber == 0){
                binding.back.visibility = View.GONE;
                binding.progressBar.setProgress(0);
            }
        }
        binding.progressBar.visibility = View.VISIBLE;
        binding.progressBar.setProgress(ceil(surveyDO.currentQuestionNumber*100.00/(surveyDO.questionDOS.size + 1)).toInt());
        binding.loading.visibility= View.GONE;
        (activity as MainActivity?)!!.scrollToTop();
        Log.e("DEBUG", surveyDO.currentQuestionNumber.toString())
    }
    private fun showSurveyComplete(){
//        binding.progressBar.visibility = View.GONE;
        binding.loading.visibility= View.GONE;
        binding.meme.setImageResource(imageIds[(0 until (imageIds.size-1)).random()])
        binding.prompt.text = "Survey Complete! Here's a cute animal as thanks."
        binding.meme.visibility = View.VISIBLE;
        binding.options.visibility = View.GONE;
        binding.sleepData.visibility = View.GONE;
        binding.back.text = "Redo Survey";
    }

    private fun sleepBoundsCompute() {
        val job = SleepExtractor.computeSleepBoundsAsync(1, requireContext().applicationContext)
        job.invokeOnCompletion {
            val pair = job.getCompleted()
            val cal = Calendar.getInstance()
            cal.timeInMillis = pair.first
            binding.sleepTime.hour = cal.get(Calendar.HOUR)
            binding.sleepTime.minute = cal.get(Calendar.MINUTE)
            Log.e("MDTKR_SLEEP_INTERNAL", "sleep: ${cal.get(Calendar.HOUR)}, ${cal.get(Calendar.MINUTE)}")

            cal.timeInMillis = pair.second
            binding.wakeUpTime.hour = cal.get(Calendar.HOUR)
            binding.wakeUpTime.minute = cal.get(Calendar.MINUTE)
            Log.e("MDTKR_SLEEP_INTERNAL", "wake: ${cal.get(Calendar.HOUR)}, ${cal.get(Calendar.MINUTE)}")
        }
    }

    private fun handleOptionClick(v:View ) {
        surveyDO.getCurrentQuestion().answer =
        surveyDO.getCurrentQuestion().optionDOS[v.getTag( R.string.buttonIdForTag) as Int]; // Tag 0 is the id of the option

        surveyDO.currentQuestionNumber += 1;
        setQuestion();
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showPopup(v: View) {
        val popup = PopupMenu(activity, v)

        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.navigation_popup, popup.menu)
        popup.show()
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

    fun goToDev(){
        switchFragment(FirstFragment())
    }

}