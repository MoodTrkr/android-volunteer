package com.example.moodtrackr.userInterface.survey

import android.content.Context
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.moodtrackr.MainActivity
import com.example.moodtrackr.R
import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.databinding.SurveyFragmentBinding
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.db.records.UsageRecordsDAO
import com.example.moodtrackr.extractors.sleep.data.MTSleepData
import com.example.moodtrackr.router.RestClient
import com.example.moodtrackr.sleepextractor.SleepExtractor
import com.example.moodtrackr.userInterface.animations.Animations
import com.example.moodtrackr.util.DatabaseManager
import com.example.moodtrackr.util.DatesUtil
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import java.sql.Date
import java.time.*
import java.util.*
import kotlin.math.ceil

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
    private var sleepQuestionNumber: Int = 0;

    private val imageIds = arrayOf(R.drawable.cute_animal_0, R.drawable.cute_animal_1,
        R.drawable.cute_animal_2, R.drawable.cute_animal_3)

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

        usageRecordsDao =
            DatabaseManager.getInstance(requireActivity().applicationContext).usageRecordsDAO;
        var rtRecordsDao =
            DatabaseManager.getInstance(requireActivity().applicationContext).rtUsageRecordsDAO;
        var rtRecord: RTUsageRecord?;
        runBlocking {
            usageRecord = usageRecordsDao.getObjOnDay(convertedDate.time)
            rtRecord = rtRecordsDao.getObjOnDay(convertedDate.time)
        }

        surveyDO = SurveyDO();
        completeQuestionNumber = surveyDO.questionDOS.size + 1
        sleepQuestionNumber = surveyDO.questionDOS.size
        if (rtRecord == null) {
            // we start creating rtRecords on the day the app is first installed.
            clearCard();
            showSurveyNotReady();
        } else {
            surveyComplete = usageRecord?.surveyData?.complete

            if (surveyComplete == true) {
                // survey is complete!
                showSurveyComplete()
            } else {
                setQuestion();
            }
        }

        binding.noSleep.setOnClickListener {
            var sleepDateTime = LocalDateTime.now()
            surveyDO.sleepData =
                MTSleepData(sleepDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                    sleepDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            surveyDO.currentQuestionNumber += 1;
            setQuestion();
        }
        binding.restart.setOnClickListener {
            surveyDO = SurveyDO()
            Animations.fadeToGone(400, binding.meme)
            Animations.fadeToGone(400, binding.restart)
            Animations.fadeIn(400, binding.progressBar)
            binding.prompt.apply {
                alpha = 1f
                visibility = View.VISIBLE

                animate()
                    .alpha(0f)
                    .setDuration(400)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            binding.prompt.visibility = View.INVISIBLE
                            setQuestion();
                        }
                    })
            }
            surveyComplete = false
        }

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
        binding.complete.setOnClickListener {
            // We are assuming wakeups are all on the same day
            // and sleep times are on the previous day if pm, the next day if am.
            // Sketchy assumption. people could sleep yesterday morning...
            var sleepDay =
                if (binding.sleepTime.hour < 12) LocalDate.now() else LocalDate.now().minusDays(1);
            var sleepTime = LocalDateTime.of(sleepDay,
                LocalTime.of(binding.sleepTime.hour, binding.sleepTime.minute));

            var wakeupTime = LocalDateTime.of(LocalDate.now(),
                LocalTime.of(binding.wakeUpTime.hour, binding.wakeUpTime.minute));

            surveyDO.sleepData =
                MTSleepData(sleepTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                    wakeupTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            surveyDO.currentQuestionNumber += 1;
            setQuestion();
        };
        binding.optionOne.setTag(R.string.buttonIdForTag, 0);
        binding.optionTwo.setTag(R.string.buttonIdForTag, 1);
        binding.optionThree.setTag(R.string.buttonIdForTag, 2);
        binding.optionFour.setTag(R.string.buttonIdForTag, 3);
        binding.optionFive.setTag(R.string.buttonIdForTag, 4);
        binding.optionSix.setTag(R.string.buttonIdForTag, 5);

        return view;
    }

    private fun clearCard() {
        binding.sleepData.visibility = View.GONE;
        binding.meme.visibility = View.GONE;
        binding.options.visibility = View.GONE;
        binding.back.visibility = View.GONE;
    }

    private fun setQuestionData() {
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

        if (surveyDO.currentQuestionNumber == 0) {
            binding.back.visibility = View.GONE;
            binding.progressBar.setProgress(0);
        }
    }

    private fun transitionQuestion() {
        Animations.fadeToInvisible(400, binding.options)
        Animations.fadeToInvisible(400, binding.back)
        binding.prompt.apply {
            // Set the content view to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            alpha = 1f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                .alpha(0f)
                .setDuration(400)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.prompt.visibility = View.INVISIBLE
                        setQuestionData()
                        showQuestion()
                    }
                })
        }
    }

    private fun transitionToSleep() {
        Animations.fadeToGone(400, binding.options)
        Animations.fadeToInvisible(400, binding.back)
        binding.prompt.apply {
            // Set the content view to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            alpha = 1f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                .alpha(0f)
                .setDuration(400)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.prompt.visibility = View.INVISIBLE
                        setSleepPageData()
                        showSleepPage()
                    }
                })
        }
    }

    private fun transitionToComplete() {
        Animations.fadeToGone(400, binding.sleepData)
        Animations.fadeToInvisible(300, binding.progressBar)
        Animations.fadeToInvisible(400, binding.back)
        binding.prompt.apply {
            // Set the content view to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            alpha = 1f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                .alpha(0f)
                .setDuration(400)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.prompt.visibility = View.INVISIBLE
                        setCompletePageData()
                        showSurveyComplete()
                    }
                })
        }
    }

    private fun transitionSleepToQuestion() {
        Animations.fadeToGone(400, binding.sleepData)
        Animations.fadeToInvisible(400, binding.back)
        binding.prompt.apply {
            // Set the content view to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            alpha = 1f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                .alpha(0f)
                .setDuration(400)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.prompt.visibility = View.INVISIBLE
                        setQuestionData()
                        showQuestion()
                    }
                })
        }
    }

    private fun setSleepPageData() {
        binding.back.text = "Previous question"
        binding.prompt.text = "Sleep Quality";
        clearCard();
    }

    private fun setCompletePageData() {
        binding.loading.visibility = View.VISIBLE;
        var surveyData = surveyDO.getSurveyData();
        surveyComplete = true;

        runBlocking {
            launch {
                usageRecord!!.surveyData = surveyData;
                usageRecordsDao.update(usageRecord!!)
            }
        }
        binding.loading.visibility = View.GONE;
    }

    private fun showQuestion() {

        binding.optionOne.alpha = 0f
        binding.optionTwo.alpha = 0f
        binding.optionThree.alpha = 0f
        binding.optionFour.alpha = 0f
        binding.optionFive.alpha = 0f
        binding.optionSix.alpha = 0f
        binding.options.alpha = 1f
        binding.options.visibility = View.VISIBLE
        Animations.fadeInStaggered(400, 120, listOf(
            binding.prompt,
            binding.optionOne,
            binding.optionTwo,
            binding.optionThree,
            binding.optionFour,
            binding.optionFive,
            binding.optionSix,
            binding.back,
        ))
    }

    private fun showSleepPage() {
        Animations.fadeInStaggered(400, 120, listOf(
            binding.prompt,
            binding.sleepData,
            binding.complete,
            binding.noSleep,
            binding.back,
        ))
    }

    private fun setQuestion() {
        if (surveyDO.currentQuestionNumber == sleepQuestionNumber) {
            // Questions finished, need sleep time
            transitionToSleep()
        } else if (surveyDO.currentQuestionNumber == completeQuestionNumber) {
            // survey is finished

            binding.loading.visibility = View.VISIBLE;
            var surveyData = surveyDO.getSurveyData();
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
            binding.loading.visibility = View.GONE;
            transitionToComplete()
        } else if (surveyDO.currentQuestionNumber == 0) {
            setQuestionData()
            showQuestion()
        } else if (surveyDO.currentQuestionNumber == sleepQuestionNumber - 1) {
            transitionSleepToQuestion()
        } else {
            if (binding.progressBar.visibility == View.INVISIBLE) {
                Animations.fadeIn(300, binding.progressBar)
            }
            transitionQuestion();
        }

        val progressPercent =
            (ceil(surveyDO.currentQuestionNumber * 100.00 / (surveyDO.questionDOS.size + 1)).toInt());
        ObjectAnimator.ofInt(binding.progressBar, "progress", progressPercent)
            .setDuration(300)
            .start();
        binding.loading.visibility = View.GONE;
        (activity as MainActivity?)!!.scrollToTop();
        Log.e("DEBUG", "showing question " + surveyDO.currentQuestionNumber.toString())

    }
    private fun showSurveyComplete(){
        clearCard()
        if(binding.progressBar.visibility == View.VISIBLE){
            binding.progressBar.visibility = View.INVISIBLE
        }
        binding.loading.visibility= View.GONE;
        binding.meme.setImageResource(imageIds[(0 until (imageIds.size-1)).random()])
        binding.prompt.text = "Survey Complete! Here's a cute animal as thanks."
        Animations.fadeInStaggered(400,120,listOf(binding.prompt, binding.meme,binding.restart))

    }

    private fun sleepBoundsCompute() {
        val job = SleepExtractor.computeSleepBoundsAsync(1, requireContext().applicationContext)
        job.invokeOnCompletion {
            val pair = job.getCompleted()
            val cal = Calendar.getInstance()
            cal.timeInMillis = pair.first
            binding.sleepTime.hour = cal.get(Calendar.HOUR)
            binding.sleepTime.minute = cal.get(Calendar.MINUTE)
            Log.e("MDTKR_SLEEP_INTERNAL",
                "sleep: ${cal.get(Calendar.HOUR)}, ${cal.get(Calendar.MINUTE)}")

            cal.timeInMillis = pair.second
            binding.wakeUpTime.hour = cal.get(Calendar.HOUR)
            binding.wakeUpTime.minute = cal.get(Calendar.MINUTE)
            Log.e("MDTKR_SLEEP_INTERNAL",
                "wake: ${cal.get(Calendar.HOUR)}, ${cal.get(Calendar.MINUTE)}")
        }
    }
        
    private fun showSurveyNotReady(){
        binding.progressBar.visibility = View.GONE;
        binding.prompt.text = "Thank you for installing! A fresh new survey will be right here tomorrow."
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