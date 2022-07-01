package com.example.moodtrackr.survey

class QuestionDO(val prompt:String, val optionDOS:Array<OptionDO>, var answer: OptionDO? = null )