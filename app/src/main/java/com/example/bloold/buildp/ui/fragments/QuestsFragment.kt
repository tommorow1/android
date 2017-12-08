package com.example.bloold.buildp.ui.fragments

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.bloold.buildp.R
import com.example.bloold.buildp.adapter.QuestsAdapter
import com.example.bloold.buildp.api.ServiceGenerator
import com.example.bloold.buildp.api.data.BaseResponseWithDataObject
import com.example.bloold.buildp.api.data.BaseResponseWithoutData
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.common.RxHelper
import com.example.bloold.buildp.common.Settings
import com.example.bloold.buildp.components.LazyScrollPageUploader
import com.example.bloold.buildp.components.NetworkFragment
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.components.UIHelper
import com.example.bloold.buildp.databinding.FragmentQuestsBinding
import com.example.bloold.buildp.databinding.FragmentSuggestionsBinding
import com.example.bloold.buildp.model.Quest
import com.example.bloold.buildp.ui.MainActivity
import com.example.bloold.buildp.ui.QuestDetailsActivity
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.android.synthetic.main.activity_catalog_object_details.*
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownHostException

class QuestsFragment : NetworkFragment(), LazyScrollPageUploader.OnLazyScrollUploaderListener {
    private lateinit var mBinding: FragmentQuestsBinding
    private lateinit var questAdapter: QuestsAdapter
    private var lazyScrollPageUploader = LazyScrollPageUploader(this)
    private var questStatus:String?=null

    companion object {
        private val ITEMS_ON_PAGE = 5

        fun newInstance() = QuestsFragment() //Квесты
        fun newInstance(questStatus:String?) //Мои квесты
                = QuestsFragment()
                .apply { arguments = Bundle().apply { putString(IntentHelper.EXTRA_QUEST_STATUS, questStatus) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questAdapter = QuestsAdapter( OnItemClickListener {
                    startActivity(Intent(activity, QuestDetailsActivity::class.java)
                            .putExtra(IntentHelper.EXTRA_QUEST_ID, it.id))
                })
        arguments?.let { questStatus=it.getString(IntentHelper.EXTRA_QUEST_STATUS) }
        if(!Settings.userToken.isNullOrEmpty())
        {
            //Значит авторизован, добавляем слушатель на кнопку присоединения к квесту
            questAdapter.onParticipateClickListener= OnItemClickListener {
                questAdapter.participateToQuest(it, !it.isParticipate)
                getCompositeDisposable().add(ServiceGenerator.serverApi.toggleQuestParticipate(it.id)
                                .compose(RxHelper.applySchedulers())
                                .subscribeWith(object : DisposableSingleObserver<Response<BaseResponseWithoutData>>() {
                                    override fun onSuccess(result: Response<BaseResponseWithoutData>) {
                                        if(result.isSuccessful&&result.body()?.code==200)
                                        {
                                            //Всё хорошо, ничего не делаем
                                        }
                                        else
                                        {
                                            UIHelper.showServerError(result, activity)
                                            loadQuests(1)
                                        }
                                    }

                                    override fun onError(e: Throwable) {
                                        loadQuests(1)
                                        e.printStackTrace()
                                        if (e is UnknownHostException || e is ConnectException)
                                            Toast.makeText(activity, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                                        else
                                            Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show()
                                    }
                                }))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_quests, container, false)
        return mBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvCatalogObjects.addOnScrollListener(lazyScrollPageUploader)
        mBinding.rvCatalogObjects.layoutManager=LinearLayoutManager(activity)
        mBinding.rvCatalogObjects.adapter=questAdapter

        showProgress(true)
        loadQuests(1)
    }

    override fun onResume() {
        super.onResume()
        if(parentFragment==null)
            activity?.toolbar?.setTitle(R.string.quests)
    }
    private fun showProgress(showProgress: Boolean) {
        mBinding.pbLoading.visibility = if (showProgress) View.VISIBLE else View.GONE
    }
    private fun updateNoItemsView() {
        if (mBinding.rvCatalogObjects.adapter.itemCount == 0) {
            mBinding.rvCatalogObjects.visibility = View.GONE
            mBinding.tvNothingToShow.visibility = View.VISIBLE
        } else {
            mBinding.rvCatalogObjects.visibility = View.VISIBLE
            mBinding.tvNothingToShow.visibility = View.GONE
        }
    }

    override fun onLoadData(totalItems: Int) {
        loadQuests(totalItems/ITEMS_ON_PAGE+1)
    }

    private fun loadQuests(page: Int)
    {
        getCompositeDisposable().add(
                (if(questStatus==null) ServiceGenerator.serverApi.getQuests(ITEMS_ON_PAGE, page)
                        else ServiceGenerator.serverApi.getMyQuests(questStatus!!, ITEMS_ON_PAGE, page))
                .compose(RxHelper.applySchedulers())
                .doOnSubscribe { lazyScrollPageUploader.setLoading(true) }
                .doFinally {
                    showProgress(false)
                    lazyScrollPageUploader.setLoading(false)
                    updateNoItemsView()
                }
                .subscribeWith(object : DisposableSingleObserver<Response<BaseResponseWithDataObject<Quest>>>() {
                    override fun onSuccess(result: Response<BaseResponseWithDataObject<Quest>>) {
                        if(result.isSuccessful&&result.body()?.code==200)
                        {
                            result.body()?.data?.items?.let {
                                val allItemsLoaded = it.size < ITEMS_ON_PAGE
                                questAdapter.isShowLoadingFooter = !allItemsLoaded
                                lazyScrollPageUploader.noMoreElements=allItemsLoaded

                                if(page==1)
                                    questAdapter.setData(it)
                                else
                                    questAdapter.addData(it)
                            }
                        }
                        else
                        {
                            UIHelper.showServerError(result, activity)
                            questAdapter.isShowLoadingFooter = false
                            lazyScrollPageUploader.noMoreElements=true
                        }
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
}
