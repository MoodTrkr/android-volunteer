package com.example.moodtrackr.userInterface.survey
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moodtrackr.R
import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.databinding.SurveyFragmentBinding
import com.example.moodtrackr.db.records.UsageRecordsDAO
import com.example.moodtrackr.util.DatabaseManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.ceil
import kotlin.random.Random

class SurveyFragment  : Fragment(R.layout.survey_fragment) {

    private lateinit var surveyDO: SurveyDO;
    private var _binding: SurveyFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var usageRecordsDao: UsageRecordsDAO
    private var usageRecord: MTUsageData? = null
    private val imageIds = arrayOf(R.drawable.cute_animal_0,R.drawable.cute_animal_1,
        R.drawable.cute_animal_2, R.drawable.cute_animal_3 )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SurveyFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        surveyDO = SurveyDO();
        val currentQuestion = surveyDO.getCurrentQuestion();
        usageRecordsDao = DatabaseManager.getInstance(requireActivity().applicationContext).usageRecordsDAO;
        runBlocking {
            usageRecord = usageRecordsDao.getObjOnDay(surveyDO.getSurveyData().time!!)
        }




        if(usageRecord?.surveyData != null){
            // survey is complete!
            showSurveyComplete()
        }else {
            setQuestion();

            binding.back.setOnClickListener {
                surveyDO.currentQuestionNumber -= 1;
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
        }
        return view;
    }

    private fun setQuestion(){
        if(surveyDO.currentQuestionNumber == surveyDO.questionDOS.size){
            // survey is finished
            binding.progressBar.setProgress(100);
            showSurveyComplete();
            var surveyData =  surveyDO.getSurveyData();

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
            binding.meme.visibility = View.GONE;
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
            binding.progressBar.setProgress(ceil(surveyDO.currentQuestionNumber*100.00/surveyDO.questionDOS.size).toInt());
        }

    }
    private fun showSurveyComplete(){
        binding.meme.setBackgroundResource(imageIds[Random.nextInt(imageIds.size)])
        binding.prompt.text = "Survey Complete! Here's a cute animal as thanks."
        binding.meme.visibility = View.VISIBLE;
        binding.options.visibility = View.GONE;
        binding.back.visibility = View.GONE;
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

}