package com.example.bloold.buildp.ui

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bloold.buildp.R
import com.example.bloold.buildp.common.BindingHelper
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.databinding.ActivitySuggestionsDetailsBinding
import com.example.bloold.buildp.databinding.ItemSuggestionBinding
import com.example.bloold.buildp.databinding.LayoutSuggestionWasNewBinding
import com.example.bloold.buildp.model.Suggestion
import java.text.SimpleDateFormat
import java.util.*


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SuggestionDetailsActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySuggestionsDetailsBinding
    private lateinit var suggestion: Suggestion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_suggestions_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        suggestion = intent.getParcelableExtra(IntentHelper.EXTRA_SUGGESTION)
        updateUI()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun updateUI()
    {
        mBinding.tvObjectName.text=suggestion.objectName
        mBinding.tvSuggestionType.setText(R.string.object_changed)
        suggestion.dateCreate?.let {
            mBinding.tvDate.text= SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(it)
        }
        BindingHelper.configureSuggestionStatusBadge(suggestion, mBinding.llStatus,
                mBinding.ivStatus, mBinding.tvStatus)
        Glide.with(this)
                .load(suggestion.objectPicture?.fullPath())
                .apply(RequestOptions().circleCrop())
                .into(mBinding.ivObject)
        suggestion.diffList?.forEach {
            //string|info|condition|element|photos|videos|audio|docs|elements|array
            when(it.type)
            {
                "string" -> {
                    val suggestionLayoutString = DataBindingUtil.inflate<LayoutSuggestionWasNewBinding>(LayoutInflater.from(this), R.layout.layout_suggestion_was_new, mBinding.llChanges, false)
                    suggestionLayoutString.tvChanged.text=it.title
                    suggestionLayoutString.tvWas.text=it.diffBody.wasValue
                    suggestionLayoutString.tvBecome.text=it.diffBody.newValue
                    mBinding.llChanges.addView(suggestionLayoutString.root)
                }
                "array" -> {
                    val suggestionLayoutString = DataBindingUtil.inflate<LayoutSuggestionWasNewBinding>(LayoutInflater.from(this), R.layout.layout_suggestion_was_new, mBinding.llChanges, false)
                    suggestionLayoutString.tvChanged.text=it.title
                    suggestionLayoutString.tvWas.text=it.diffBody.wasValue
                    suggestionLayoutString.tvBecome.text=it.diffBody.newValue
                    mBinding.llChanges.addView(suggestionLayoutString.root)
                }
            }
        }
    }
}
