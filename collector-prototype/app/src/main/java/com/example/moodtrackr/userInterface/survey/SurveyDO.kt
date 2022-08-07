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
                    OptionDO(0, "Extremely depressed, I felt emotionally numb, could not function at all, and did not wish to live"),
                    OptionDO(1, "Very depressed, had feelings of hopelessness, and functioned very poorly"),
                    OptionDO(2, "Moderately depressed. Found it difficult to brighten up and did not function well"),
                    OptionDO(3, "Mildly depressed. Felt more depressed than usual, taking some difficulty to brighten up, but could function"),
                    OptionDO(4, "Slightly Depressed. Felt a little down, but it did not impair me in any way"),
                    OptionDO(5, "No feelings of depression"),
                )
            ),
            QuestionDO(
                "Did you feel symptoms of elevation yesterday? Select the most extreme description of how you felt.",
                arrayOf(
                    OptionDO(0, "Extremely elevated. Could not control self or function at all, making many reckless decisions"),
                    OptionDO(1, "Very elevated. Feel very happy. Exhibited inappropriate laughing, could control self only briefly and functioned very poorly"),
                    OptionDO(2, "Moderately elevated. Felt significantly more cheerful than normal, found difficulty with self-control and did not function well"),
                    OptionDO(3, "Mildly elevated.  Felt more cheerful than normal, but could control self and function"),
                    OptionDO(4, "Slightly elevated. Felt slightly cheerful, but it did not impair me in any way"),
                    OptionDO(5, "No feelings of elevation"),
                )
            ),
            QuestionDO(
                "Did you feel symptoms of anger yesterday? Select the most extreme description of how you felt.",
                arrayOf(
                    OptionDO(0, "Extremely angry. Felt constant anger, could not be calmed down, control self, or function at all"),
                    OptionDO(1, "Very elevated. Feel very happy. Exhibited inappropriate laughing, could control self only briefly and functioned very poorly"),
                    OptionDO(2, "Moderately angry. Felt significantly more angry than normal, found difficulty with self-control and did not function well"),
                    OptionDO(3, "Mildly angry.  Felt more anger than normal, but could control self and function"),
                    OptionDO(4, "Slightly angry. Felt slightly angry, but it did not impair me in any way"),
                    OptionDO(5, "No feelings of anger"),
                )
            ),
            QuestionDO(
                "Did you feel symptoms of anxiety yesterday? Select the most extreme description of how you felt.",
                arrayOf(
                    OptionDO(0, "Extremely anxious. Felt constant anxiety, anxiety attacks, could not be calmed down, control self, or function at all"),
                    OptionDO(1, "Very anxious. Could control self only briefly and functioned very poorly as a result"),
                    OptionDO(2, "Moderately anxious. Felt significantly more anxious than normal, found difficulty with self-control and did not function well"),
                    OptionDO(3, "Mildly anxious.  Felt more anxious than normal, but could control self and function"),
                    OptionDO(4, "Slightly anxious. Felt slightly anxious, but it did not impair me in any way"),
                    OptionDO(5, "No feelings of anxiety"),
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
