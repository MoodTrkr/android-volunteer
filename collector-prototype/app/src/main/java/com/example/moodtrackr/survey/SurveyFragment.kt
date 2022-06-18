package com.example.moodtrackr.survey
import android.os.Bundle
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
        val currentQuestion = survey.questions[survey.currentQuestion];

        binding.optionOne.text = currentQuestion.options[0].text;
        binding.optionTwo.text = currentQuestion.options[0].text;



        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}