package com.example.bloold.buildp.ui

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.bloold.buildp.MyApplication
import com.example.bloold.buildp.R
import com.example.bloold.buildp.adapter.FilterAdapter
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.common.Settings
import com.example.bloold.buildp.databinding.ActivityChooseFiltersBinding
import com.example.bloold.buildp.model.Category


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ChooseFiltersActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityChooseFiltersBinding
    private lateinit var filterAdapter:FilterAdapter

    companion object {
        val REQUEST_CODE_EDIT_FILTERS=127
        fun launch(act:Activity, category: Category?, code:Int)
        {
            act.startActivityForResult(Intent(act, ChooseFiltersActivity::class.java)
                    .putExtra(IntentHelper.EXTRA_SORT_OBJECT, category), code)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_choose_filters)
        mBinding.listener=this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val dynamicFilters: Category? = intent.getParcelableExtra(IntentHelper.EXTRA_SORT_OBJECT)

        val staticFilters= Category(getString(R.string.extra_static_filters)).apply {
            children=arrayOf(Category(MyApplication.instance.getString(R.string.filter_unesco),"PROPERTY_UNESCO"),
                    Category(MyApplication.instance.getString(R.string.filter_vip),"PROPERTY_VALUABLE"),
                    Category(MyApplication.instance.getString(R.string.filter_history),"PROPERTY_HISTORIC_SETTLEMENT"),
                    Category(MyApplication.instance.getString(R.string.filter_quest),"PROPERTY_QUEST_ID"),
                    Category(MyApplication.instance.getString(R.string.filter_vote),"PROPERTY_VOTING_ID")
            )}
        val fullFilter = ArrayList<Category>()

        dynamicFilters?.let { if(it.children?.isNotEmpty()==true) fullFilter.add(it) }
        fullFilter.add(staticFilters)
        filterAdapter= FilterAdapter(this, fullFilter.toTypedArray())
        Settings.catalogFilters?.let { filterAdapter.selectedIds.addAll(it) }
        mBinding.elvFilters.setAdapter(filterAdapter)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun onApplyFiltersClick(v:View)
    {
        Settings.catalogFilters=null
        Settings.catalogFilters=filterAdapter.selectedIds
        setResult(Activity.RESULT_OK)
        finish()
    }
}
