package com.example.bloold.buildp.ui

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bloold.buildp.R
import com.example.bloold.buildp.adapter.CatalogObjectDetailsPagerAdapter
import com.example.bloold.buildp.api.ApiHelper
import com.example.bloold.buildp.api.ServiceGenerator
import com.example.bloold.buildp.api.data.BaseResponseWithDataObject
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.common.RxHelper
import com.example.bloold.buildp.common.Settings
import com.example.bloold.buildp.components.NetworkActivity
import com.example.bloold.buildp.databinding.ActivityQuestDetailsBinding
import com.example.bloold.buildp.model.Quest
import com.example.bloold.buildp.single.`object`.DescriptionFragment
import com.example.bloold.buildp.ui.fragments.ObjectsInQuestFragment
import com.example.bloold.buildp.ui.fragments.ParticipantsInQuestFragment
import com.example.bloold.buildp.ui.fragments.QuestsFragment
import io.reactivex.observers.DisposableSingleObserver
import java.net.ConnectException
import java.net.UnknownHostException


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class QuestDetailsActivity : NetworkActivity() {
    private lateinit var mBinding: ActivityQuestDetailsBinding
    private lateinit var questDetailsPagerAdapter: QuestDetailPagerAdapter
    private var quest: Quest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_quest_details)
        mBinding.listener=this

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loadQuestDetails(intent.getIntExtra(IntentHelper.EXTRA_QUEST_ID, 0))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun updateUI()
    {
        quest?.let {
            questDetailsPagerAdapter=QuestDetailPagerAdapter(supportFragmentManager)
            mBinding.viewPager.adapter = questDetailsPagerAdapter
            mBinding.tabs.setupWithViewPager(mBinding.viewPager)

            Glide.with(this)
                    .load(it.pictureDetail.fullPath())
                    .apply(RequestOptions().centerCrop())
                    .into(mBinding.ivBackdrop)
            mBinding.collapsingToolbar.title = it.name
        }
    }
    private fun showProgress(showProgress: Boolean) {
        mBinding.flLoading.visibility = if (showProgress) View.VISIBLE else View.GONE
    }

    private fun loadQuestDetails(questId:Int)
    {
        compositeDisposable.add(ServiceGenerator.serverApi.getQuestDetails(questId)
                .compose(RxHelper.applySchedulers())
                .doFinally {
                    if(quest!=null)
                    {
                        updateUI()
                        showProgress(false)
                    }
                    else finish()
                }
                .doOnSubscribe { showProgress(true) }
                .subscribeWith(object : DisposableSingleObserver<BaseResponseWithDataObject<Quest>>() {
                    override fun onSuccess(result: BaseResponseWithDataObject<Quest>) {
                        quest=result.data?.items?.firstOrNull()
                    }
                    override fun onError(e: Throwable)
                    {
                        e.printStackTrace()
                        if (e is UnknownHostException || e is ConnectException)
                            Toast.makeText(applicationContext, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(applicationContext, R.string.server_error, Toast.LENGTH_SHORT).show()
                    }
                }))
    }

    private inner class QuestDetailPagerAdapter internal constructor(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getItem(i: Int): Fragment? {
            return when (i) {
                0 -> DescriptionFragment.newInstance(quest?.detailText)
                1 -> ObjectsInQuestFragment.newInstance(quest?.objects)
                2 -> ParticipantsInQuestFragment.newInstance(quest?.participantsData)
                else -> null
            }
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(R.string.description)
                1 -> getString(R.string.objects)
                2 -> getString(R.string.participants)
                else -> null
            }
        }
    }

}
