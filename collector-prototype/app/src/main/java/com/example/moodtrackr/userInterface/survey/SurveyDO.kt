package com.example.moodtrackr.userInterface.survey
import com.example.moodtrackr.data.SurveyData
import com.example.moodtrackr.extractors.sleep.data.MTSleepData
import com.example.moodtrackr.util.DatesUtil
import java.sql.Date
import java.time.LocalDate
import java.time.ZoneOffset

class SurveyDO (
    var currentQuestionNumber:Int = 0,
    val questionDOS:Array<QuestionDO> =
        arrayOf(
            QuestionDO(
                "Have you been bothered by nervousness or your \"nerves\" during \n" +
                    "the past month?",
                arrayOf(
                    OptionDO(0, "Extremely so – to the point where I could not work or take care of things"),
                    OptionDO(1, "Very much so"),
                    OptionDO(2, "Quite a bit"),
                    OptionDO(3, "Some - enough to bother me"),
                    OptionDO(4, "A little"),
                    OptionDO(5, "Not at all"),
                )
            ),
            QuestionDO(
                "How much energy, pep, or vitality did you have or feel during\n" +
                        "the past month?",
                arrayOf(
                    OptionDO(0, "No energy or pep at all – I fell drained, sapped"),
                    OptionDO(1, "Very low in energy or pep most of the time"),
                    OptionDO(2, "Generally low in energy or pep"),
                    OptionDO(3, "My energy level varied quite a bit"),
                    OptionDO(4, "Fairly energetic most of the time"),
                    OptionDO(5, "Very full of energy – lots of pep"),
                )
            )

        ),
    val date: LocalDate = LocalDate.now().minusDays(1),
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
        var convertedDate = Date.from(this.date.atStartOfDay().toInstant(ZoneOffset.UTC));
        convertedDate = DatesUtil.truncateDate(convertedDate);
        return SurveyData(convertedDate,surveyVersion,answers,isComplete, MTSleepData())
    }
}
