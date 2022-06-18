package com.example.moodtrackr.survey
import java.time.LocalDate

class Survey (
    val currentQuestion:Int = 0,
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
            )
        ),
    val date: LocalDate = LocalDate.now().minusDays(1),
)