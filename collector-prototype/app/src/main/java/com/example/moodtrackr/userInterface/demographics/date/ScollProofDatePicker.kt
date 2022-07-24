package com.example.moodtrackr.userInterface.demographics.date

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewParent
import android.widget.DatePicker


class ScollProofDatePicker(context: Context, attrs: AttributeSet): DatePicker(context, attrs) {
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {

        // Stop ScrollView from getting involved once you interact with the View
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            val p: ViewParent = this.parent
            if (p != null) {
                p.requestDisallowInterceptTouchEvent(true)
            }
        }
        return false
    }
}