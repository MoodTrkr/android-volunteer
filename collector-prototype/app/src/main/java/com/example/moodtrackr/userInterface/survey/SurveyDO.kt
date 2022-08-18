package com.example.moodtrackr.userInterface.survey
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import com.example.moodtrackr.R
import com.example.moodtrackr.data.SurveyData
import com.example.moodtrackr.extractors.sleep.data.MTSleepData
import com.example.moodtrackr.util.DatesUtil
import java.sql.Date
import java.time.*
import java.util.*

class SurveyDO (
    var sleepData:MTSleepData = MTSleepData(),
    var currentQuestionNumber:Int = 0,
    var questionDOS:Array<QuestionDO> =
        arrayOf(
            QuestionDO(
                "Did you feel symptoms of depression yesterday? Select the most extreme description of how you felt.",
                arrayOf(
                    OptionDO(0, "No feelings of depression"),
                    OptionDO(1, "Slightly Depressed. Felt a little down, but it did not impair me in any way"),
                    OptionDO(2, "Mildly depressed. Felt more depressed than usual, taking some difficulty to brighten up, but could function"),
                    OptionDO(3, "Moderately depressed. Found it difficult to brighten up and did not function well"),
                    OptionDO(4, "Very depressed, had feelings of hopelessness, and functioned very poorly"),
                    OptionDO(5, "Extremely depressed, I felt emotionally numb, could not function at all, and did not wish to live"),
                )
            ),
            QuestionDO(
                "Did you feel symptoms of elevation yesterday? Select the most extreme description of how you felt.",
                arrayOf(
                    OptionDO(0, "No feelings of elevation"),
                    OptionDO(1, "Slightly elevated. Felt slightly cheerful, but it did not impair me in any way"),
                    OptionDO(2, "Mildly elevated.  Felt more cheerful than normal, but could control self and function"),
                    OptionDO(3, "Moderately elevated. Felt significantly more cheerful than normal, found difficulty with self-control and did not function well"),
                    OptionDO(4, "Very elevated. Feel very happy. Exhibited inappropriate laughing, could control self only briefly and functioned very poorly"),
                    OptionDO(5, "Extremely elevated. Could not control self or function at all, making many reckless decisions"),
                )
            ),
            QuestionDO(
                "How much motivation did you display yesterday? Select the most extreme description of how you felt.",
                arrayOf(
                    OptionDO(0, "Not motivated – did not possess energy to pursue goals/interests today"),
                    OptionDO(1, "Slightly motivated, had enough energy to approach interests/goals"),
                    OptionDO(2, "Mildly motivated. Had enough energy to pursue interests/goals for some time"),
                    OptionDO(3, "Moderately motivated. Had enough energy to pursue interests/goals consistently for a significant portion of time"),
                    OptionDO(4, "Very motivated. Pursued interests/goals to the point where appetite, daily responsibilities, etc were affected for most of the day"),
                    OptionDO(5, "Extremely motivated. Pursued interests/goals to the point where appetite, daily responsibilities, sleep, etc were ignored entirely"),
                )
            ),
            QuestionDO(
                "Did you feel symptoms of anger yesterday? Select the most extreme description of how you felt.",
                arrayOf(
                    OptionDO(0, "No feelings of anger"),
                    OptionDO(1, "Slightly angry. Felt slightly angry, but it did not impair me in any way"),
                    OptionDO(2, "Mildly angry.  Felt more anger than normal, but could control self and function"),
                    OptionDO(3, "Moderately angry. Felt significantly more angry than normal, found difficulty with self-control and did not function well"),
                    OptionDO(4, "Very angry. Could control self only briefly and functioned very poorly as a result"),
                    OptionDO(5, "Extremely angry. Felt constant anger, could not be calmed down, control self, or function at all"),
                )
            ),
            QuestionDO(
                "Did you feel symptoms of anxiety yesterday? Select the most extreme description of how you felt.",
                arrayOf(
                    OptionDO(0, "No feelings of anxiety"),
                    OptionDO(1, "Slightly anxious. Felt slightly anxious, but it did not impair me in any way"),
                    OptionDO(2, "Mildly anxious.  Felt more anxious than normal, but could control self and function"),
                    OptionDO(3, "Moderately anxious. Felt significantly more anxious than normal, found difficulty with self-control and did not function well"),
                    OptionDO(4, "Very anxious. Could control self only briefly and functioned very poorly as a result"),
                    OptionDO(5, "Extremely anxious. Felt constant anxiety, anxiety attacks, could not be calmed down, control self, or function at all"),
                )
            ),
            QuestionDO(
                "Were you healthy enough to continue your day as you normally would? (Non-optimal health can mean headaches, recovery from a cold, chronic pain, etc)",
                arrayOf(
                    OptionDO(0, "I needed someone to help me with most or all of the things I had to do"),
                    OptionDO(1, "I needed some help in taking care of myself"),
                    OptionDO(2, "I was only healthy enough to take care of myself, and no one else"),
                    OptionDO(3, "Health problems limited me in some important ways"),
                    OptionDO(4, "Mostly, though I was not at peak health"),
                    OptionDO(5, "Yes, I felt healthy today"),
                )
            )

        ),
    var date: LocalDateTime = LocalDateTime.now().minusDays(1),
){

    fun getCurrentQuestion(): QuestionDO {
        return questionDOS[currentQuestionNumber]
    }

    fun getSurveyData(): SurveyData{
        val answers = mutableMapOf<Int, Int>()
        val surveyVersion = 0
        var index = 0;
        var isComplete = true
        while(index < this.questionDOS.size){
            if(this.questionDOS[index].answer == null){
                isComplete = false
                break
            }
            answers[index] = this.questionDOS[index].answer!!.id
            index++
        }
        var convertedDate =  Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
        convertedDate = DatesUtil.truncateDate(convertedDate);
        return SurveyData(convertedDate,surveyVersion, answers, isComplete, this.sleepData)
    }
}
// May have bugs
//fun surveyDOFromCompleteSurveyData(surveyData: SurveyData):SurveyDO{
//        val surveyDO = SurveyDO();
//
//        val answers = surveyData.questions
//        var index = 0;
//        while(index < surveyDO.questionDOS.size){
//            surveyDO.questionDOS[index].answer =
//                surveyDO.questionDOS[index].optionDOS.find{it.id == answers[index]}
//            index++
//        }
//        surveyDO.date = Instant.ofEpochMilli(surveyData.time).atZone(ZoneId.systemDefault()).toLocalDateTime();
//        surveyDO.currentQuestionNumber = surveyDO.questionDOS.size
//       return surveyDO
//}
