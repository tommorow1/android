package com.example.bloold.buildp.common

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.bloold.buildp.R
import com.example.bloold.buildp.model.Suggestion

/**
 * Created by sagus on 01.12.2017.
 */
object BindingHelper {
    fun configureSuggestionStatusBadge(suggestion: Suggestion, badgeLayout:LinearLayout, imageView: ImageView, textView: TextView)
    {
        when(suggestion.stateName)
        {
            "На модерации" ->
            {
                badgeLayout.setBackgroundResource(R.drawable.b_flag_yellow)
                imageView.setImageResource(R.drawable.ic_more_horiz)
            }
            "Отклонено" ->
            {
                badgeLayout.setBackgroundResource(R.drawable.b_flag_red)
                imageView.setImageResource(R.drawable.ic_close_white)
            }
            "Одобрено" ->
            {
                badgeLayout.setBackgroundResource(R.drawable.b_flag_green)
                imageView.setImageResource(R.drawable.ic_done)
            }
        }
        textView.text=suggestion.stateName

    }
}