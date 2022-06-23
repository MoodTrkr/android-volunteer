package com.example.moodtrackr.survey
import java.time.LocalDate

class Survey (
    var currentQuestionNumber:Int = 0,
    val questions:Array<Question> =
        arrayOf(
            Question(
                "Have you been bothered by nervousness or your \"nerves\" during \n" +
                    "the past month?",
                arrayOf(
                    Option(0, "Extremely so – to the point where I could not work or take care of things"),
                    Option(1, "Very much so"),
                    Option(2, "Quite a bit"),
                    Option(3, "Some - enough to bother me"),
                    Option(4, "A little"),
                    Option(5, "Not at all"),
                )
            ),
            Question(
                "How much energy, pep, or vitality did you have or feel during\n" +
                        "the past month?",
                arrayOf(
                    Option(0, "No energy or pep at all – I fell drained, sapped"),
                    Option(1, "Very low in energy or pep most of the time"),
                    Option(2, "Generally low in energy or pep"),
                    Option(3, "My energy level varied quite a bit"),
                    Option(4, "Fairly energetic most of the time"),
                    Option(5, "Very full of energy – lots of pep"),
                )
            )

        ),
    val date: LocalDate = LocalDate.now().minusDays(1),
){
    fun getCurrentQuestion():Question{
        return questions[currentQuestionNumber]
    }
}
