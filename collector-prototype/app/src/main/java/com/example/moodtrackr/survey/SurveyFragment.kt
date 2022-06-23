package com.example.moodtrackr.survey
import android.os.Bundle
import android.util.Log
import android.util.Log.DEBUG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moodtrackr.R
import com.example.moodtrackr.databinding.SurveyFragmentBinding

class SurveyFragment  : Fragment(R.layout.survey_fragment) {

    private lateinit var survey:Survey;
    private var _binding: SurveyFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SurveyFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        survey = Survey();
        val currentQuestion = survey.getCurrentQuestion();

        setQuestion();

        binding.back.setOnClickListener {
                survey.currentQuestionNumber-=1;
                setQuestion();
        };
        binding.optionOne.setOnClickListener{v->
            handleOptionClick(v);
        };
        binding.optionTwo.setOnClickListener{v->
            handleOptionClick(v);
        };
        binding.optionThree.setOnClickListener{v->
            handleOptionClick(v);
        };
        binding.optionFour.setOnClickListener{v->
            handleOptionClick(v);
        };
        binding.optionFive.setOnClickListener{v->
            handleOptionClick(v);
        };
        binding.optionSix.setOnClickListener{v->
            handleOptionClick(v);
        };
        binding.optionOne.setTag( R.string.buttonIdForTag,0);
        binding.optionTwo.setTag( R.string.buttonIdForTag,1);
        binding.optionThree.setTag( R.string.buttonIdForTag,2);
        binding.optionFour.setTag( R.string.buttonIdForTag,3);
        binding.optionFive.setTag( R.string.buttonIdForTag,4);
        binding.optionSix.setTag( R.string.buttonIdForTag,5);
        return view;
    }

    private fun setQuestion(){
        if(survey.currentQuestionNumber === survey.questions.size){
            // survey is finished
            binding.prompt.text = "Survey Complete! Here's a cute animal as thanks."
            binding.meme.visibility = View.VISIBLE;
            binding.options.visibility = View.INVISIBLE;
            binding.back.visibility = View.INVISIBLE;
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

    private fun handleOptionClick(v:View ) {
        Log.e("test", "Magnamalos");
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