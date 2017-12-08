package com.example.bloold.buildp.ui.fragments

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.bloold.buildp.R
import com.example.bloold.buildp.adapter.FavouriteAdapter
import com.example.bloold.buildp.api.ServiceGenerator
import com.example.bloold.buildp.api.data.BaseResponseWithDataObject
import com.example.bloold.buildp.api.data.CatalogObject
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.common.RxHelper
import com.example.bloold.buildp.components.LazyScrollPageUploader
import com.example.bloold.buildp.components.NetworkFragment
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.databinding.FragmentCatalogObjectBinding
import com.example.bloold.buildp.model.FavouriteObject
import com.example.bloold.buildp.ui.CatalogObjectDetailsActivity
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.android.synthetic.main.app_bar_main.*
import java.net.ConnectException
import java.net.UnknownHostException

class FavouriteObjectListFragment : NetworkFragment()
{
    private lateinit var mBinding: FragmentCatalogObjectBinding
    private lateinit var catalogObjectAdapter: FavouriteAdapter

    companion object {
        private val KEY_RESPONSE_ARRAY_OBJECTS = "catalog_response"

        fun newInstance(): FavouriteObjectListFragment
            = FavouriteObjectListFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catalogObjectAdapter = FavouriteAdapter(
                OnItemClickListener {
                    startActivity(Intent(context, CatalogObjectDetailsActivity::class.java)
                            .putExtra(IntentHelper.EXTRA_OBJECT_ID, it.id))
                })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_catalog_object, container, false)
        return mBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvCatalogObjects.layoutManager=LinearLayoutManager(activity)
        mBinding.rvCatalogObjects.adapter=catalogObjectAdapter

        loadCatalogObjects()
    }

    override fun onResume() {
        super.onResume()
        activity?.toolbar?.title=getString(R.string.favourite)
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

    private fun loadCatalogObjects()
    {
        getCompositeDisposable().add(ServiceGenerator.serverApi.getFavourite()
                .compose(RxHelper.applySchedulers())
                .doOnSubscribe { showProgress(true) }
                .doFinally {
                    showProgress(false)
                    updateNoItemsView()
                }
                .subscribeWith(object : DisposableSingleObserver<BaseResponseWithDataObject<FavouriteObject>>() {
                    override fun onSuccess(result: BaseResponseWithDataObject<FavouriteObject>) {
                        result.data?.items?.let {
                            catalogObjectAdapter.setData(it)
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
