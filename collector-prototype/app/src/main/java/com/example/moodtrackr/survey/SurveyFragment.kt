package com.example.moodtrackr.survey
import android.os.Bundle
import android.util.Log
import android.util.Log.DEBUG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moodtrackr.R
import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.databinding.SurveyFragmentBinding
import com.example.moodtrackr.db.records.UsageRecordsDAO
import com.example.moodtrackr.utilities.DatabaseManager
import com.example.moodtrackr.utilities.DatesUtil
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SurveyFragment  : Fragment(R.layout.survey_fragment) {

    private lateinit var survey:Survey;
    private var _binding: SurveyFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var usageRecordsDao: UsageRecordsDAO
    private var usageRecord: MTUsageData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SurveyFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        survey = Survey();
        val currentQuestion = survey.getCurrentQuestion();
        usageRecordsDao = DatabaseManager.getInstance(requireActivity().applicationContext).usageRecordsDAO;
        runBlocking {
            usageRecord = usageRecordsDao.getObjOnDay(survey.getSurveyData().time!!)
        }
        //To DO
        // Make The complete screen show up if the survey for yesterday is complete.
        // create themes/templates

//        if(usageRecord?.surveyData != null){
//            // survey is complete!
//            showSurveyComplete()
//        }else {
            setQuestion();

            binding.back.setOnClickListener {
                survey.currentQuestionNumber -= 1;
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
            binding.optionOne.setTag(R.string.buttonIdForTag, 0);
            binding.optionTwo.setTag(R.string.buttonIdForTag, 1);
            binding.optionThree.setTag(R.string.buttonIdForTag, 2);
            binding.optionFour.setTag(R.string.buttonIdForTag, 3);
            binding.optionFive.setTag(R.string.buttonIdForTag, 4);
            binding.optionSix.setTag(R.string.buttonIdForTag, 5);
//        }
        return view;
    }

    private fun setQuestion(){
        if(survey.currentQuestionNumber == survey.questions.size){
            // survey is finished
            showSurveyComplete();
            var surveyData =  survey.getSurveyData();

            runBlocking {
                launch{

                    // Creates record for testing, remove later!!
                    if(usageRecordsDao.getObjOnDay(surveyData.time) == null){
                        var mock = MTUsageData();
                        mock.date = surveyData.time
                        usageRecordsDao.insert(mock)
                    }

                    val usageRecord = usageRecordsDao.getObjOnDay(surveyData.time);
                    if(usageRecord != null){
                        usageRecord.surveyData = surveyData;
                        usageRecordsDao.update(usageRecord)
                    }
                }
            }
        }
        else{
            binding.meme.visibility = View.INVISIBLE;
            binding.options.visibility = View.VISIBLE;
            binding.back.visibility = View.VISIBLE;
            val currentQuestion = survey.getCurrentQuestion();
            binding.optionOne.text = currentQuestion.options[0].text;
            binding.optionTwo.text = currentQuestion.options[1].text;
            binding.optionThree.text = currentQuestion.options[2].text;
            binding.optionFour.text = currentQuestion.options[3].text;
            binding.optionFive.text = currentQuestion.options[4].text;
            binding.optionSix.text = currentQuestion.options[5].text;

            binding.prompt.text = currentQuestion.prompt;

            if(survey.currentQuestionNumber == 0){
                binding.back.visibility = View.INVISIBLE;
            }
        }

    }
    private fun showSurveyComplete(){
        binding.prompt.text = "Survey Complete! Here's a cute animal as thanks."
        binding.meme.visibility = View.VISIBLE;
        binding.options.visibility = View.INVISIBLE;
        binding.back.visibility = View.INVISIBLE;
    }

    private fun handleOptionClick(v:View ) {
        survey.getCurrentQuestion().answer =
        survey.getCurrentQuestion().options[v.getTag( R.string.buttonIdForTag) as Int]; // Tag 0 is the id of the option

        survey.currentQuestionNumber += 1;
        setQuestion();
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}