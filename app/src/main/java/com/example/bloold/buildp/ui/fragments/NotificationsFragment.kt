package com.example.bloold.buildp.ui.fragments

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
import com.example.bloold.buildp.adapter.NotificationsAdapter
import com.example.bloold.buildp.api.ServiceGenerator
import com.example.bloold.buildp.api.data.BaseResponseWithDataObject
import com.example.bloold.buildp.common.RxHelper
import com.example.bloold.buildp.components.LazyScrollPageUploader
import com.example.bloold.buildp.components.NetworkFragment
import com.example.bloold.buildp.components.UIHelper
import com.example.bloold.buildp.databinding.FragmentNotificationsBinding
import com.example.bloold.buildp.model.NotificationInfo
import com.example.bloold.buildp.services.NetworkIntentService
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.android.synthetic.main.app_bar_main.*
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownHostException

class NotificationsFragment : NetworkFragment(), LazyScrollPageUploader.OnLazyScrollUploaderListener {
    private lateinit var mBinding: FragmentNotificationsBinding
    private lateinit var notificationsAdapter: NotificationsAdapter
    private var lazyScrollPageUploader = LazyScrollPageUploader(this)

    companion object {
        private val ITEMS_ON_PAGE = 5

        fun newInstance() = NotificationsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationsAdapter = NotificationsAdapter()

        activity?.baseContext?.let { NetworkIntentService.setAllNotificationsRead(it) }
    }

    override fun onResume() {
        super.onResume()
        activity?.toolbar?.setTitle(R.string.notifications)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false)
        return mBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.recyclerView.addOnScrollListener(lazyScrollPageUploader)
        mBinding.recyclerView.layoutManager=LinearLayoutManager(activity)
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, OrientationHelper.VERTICAL))
        mBinding.recyclerView.adapter=notificationsAdapter

        showProgress(true)
        loadNotifications(1)
    }
    private fun showProgress(showProgress: Boolean) {
        mBinding.pbLoading.visibility = if (showProgress) View.VISIBLE else View.GONE
    }
    private fun updateNoItemsView() {
        if (mBinding.recyclerView.adapter.itemCount == 0) {
            mBinding.recyclerView.visibility = View.GONE
            mBinding.tvNothingToShow.visibility = View.VISIBLE
        } else {
            mBinding.recyclerView.visibility = View.VISIBLE
            mBinding.tvNothingToShow.visibility = View.GONE
        }
    }

    override fun onLoadData(totalItems: Int) {
        loadNotifications(totalItems/ITEMS_ON_PAGE+1)
    }

    private fun loadNotifications(page: Int)
    {
        getCompositeDisposable().add(ServiceGenerator.serverApi.getNotifications(ITEMS_ON_PAGE, page)
                .compose(RxHelper.applySchedulers())
                .doOnSubscribe { lazyScrollPageUploader.setLoading(true) }
                .doFinally {
                    showProgress(false)
                    lazyScrollPageUploader.setLoading(false)
                    updateNoItemsView()
                }
                .subscribeWith(object : DisposableSingleObserver<Response<BaseResponseWithDataObject<NotificationInfo>>>() {
                    override fun onSuccess(result: Response<BaseResponseWithDataObject<NotificationInfo>>) {
                        if(result.isSuccessful&&result.body()?.code==200)
                        {
                            result.body()?.data?.items?.let {
                                val allItemsLoaded = it.size < ITEMS_ON_PAGE
                                notificationsAdapter.isShowLoadingFooter = !allItemsLoaded
                                lazyScrollPageUploader.noMoreElements=allItemsLoaded

                                if(page==1)
                                    notificationsAdapter.setData(it)
                                else
                                    notificationsAdapter.addData(it)
                            }
                        }
                        else
                        {
                            UIHelper.showServerError(result, activity)
                            notificationsAdapter.isShowLoadingFooter = false
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
