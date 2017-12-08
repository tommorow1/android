package com.example.bloold.buildp.ui.fragments

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.bloold.buildp.R
import com.example.bloold.buildp.api.ServiceGenerator
import com.example.bloold.buildp.api.data.QuestTypes
import com.example.bloold.buildp.common.RxHelper
import com.example.bloold.buildp.components.NetworkFragment
import com.example.bloold.buildp.components.UIHelper
import com.example.bloold.buildp.databinding.FragmentQuestsTabsBinding
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.android.synthetic.main.app_bar_main.*
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownHostException

class MyQuestsTabsFragment : NetworkFragment() {
    private lateinit var mBinding: FragmentQuestsTabsBinding
    private lateinit var suggestionPagerAdapter: QuestsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_quests_tabs, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadQuestTypes()
    }

    override fun onResume() {
        super.onResume()
        activity?.toolbar?.setTitle(R.string.my_quests)
    }

    private fun showProgress(showProgress: Boolean) {
        mBinding.flLoading.visibility = if (showProgress) View.VISIBLE else View.GONE
    }

    private fun loadQuestTypes()
    {
        getCompositeDisposable().add(ServiceGenerator.serverApi.getQuestTypes()
                .compose(RxHelper.applySchedulers())
                .doOnSubscribe { showProgress(true) }
                .doFinally {
                    showProgress(false)
                }
                .subscribeWith(object : DisposableSingleObserver<Response<QuestTypes>>() {
                    override fun onSuccess(result: Response<QuestTypes>) {
                        if(result.isSuccessful&&result.body()?.code==200)
                        {
                            result.body()?.data?.items?.let {
                                suggestionPagerAdapter = QuestsPagerAdapter(childFragmentManager, it)
                                mBinding.viewPager.adapter = suggestionPagerAdapter
                                mBinding.tabs.setupWithViewPager(mBinding.viewPager)
                            }
                        }
                        else
                            UIHelper.showServerError(result, activity)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        if (e is UnknownHostException || e is ConnectException)
                            Toast.makeText(activity, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show()
                    }
                }))
    }


    private inner class QuestsPagerAdapter(fm: FragmentManager, val types: Map<String, String>) : FragmentStatePagerAdapter(fm)
    {
        override fun getItem(i: Int): Fragment?
                = QuestsFragment.newInstance(types.keys.elementAt(i))

        override fun getCount(): Int {
            return types.size
        }

        override fun getPageTitle(position: Int): CharSequence?
            =types.values.elementAt(position)
    }

    companion object {

        fun newInstance(): MyQuestsTabsFragment {
            return MyQuestsTabsFragment()
        }
    }
}