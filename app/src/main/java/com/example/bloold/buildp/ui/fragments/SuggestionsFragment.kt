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
import com.example.bloold.buildp.adapter.SuggestionAdapter
import com.example.bloold.buildp.api.ServiceGenerator
import com.example.bloold.buildp.api.data.BaseResponseWithDataObject
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.common.RxHelper
import com.example.bloold.buildp.components.LazyScrollPageUploader
import com.example.bloold.buildp.components.NetworkFragment
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.components.UIHelper
import com.example.bloold.buildp.databinding.FragmentSuggestionsBinding
import com.example.bloold.buildp.model.Category
import com.example.bloold.buildp.model.Suggestion
import com.example.bloold.buildp.ui.MainActivity
import com.example.bloold.buildp.ui.SuggestionDetailsActivity
import io.reactivex.observers.DisposableSingleObserver
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownHostException

class SuggestionsFragment : NetworkFragment(), LazyScrollPageUploader.OnLazyScrollUploaderListener {
    private lateinit var mBinding: FragmentSuggestionsBinding
    private lateinit var suggestionsAdapter: SuggestionAdapter
    private var lazyScrollPageUploader = LazyScrollPageUploader(this)
    var category: Category? = null
    private lateinit var suggestionType:String

    companion object {
        private val ITEMS_ON_PAGE = 5

        fun newInstance(suggestionType:String) = SuggestionsFragment()
                    .apply { arguments = Bundle().apply { putString(IntentHelper.EXTRA_SUGGESTION_TYPE, suggestionType) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        suggestionsAdapter = SuggestionAdapter( OnItemClickListener {
                    startActivity(Intent(activity, SuggestionDetailsActivity::class.java)
                            .putExtra(IntentHelper.EXTRA_SUGGESTION, it))
                })
        arguments?.let { suggestionType=it.getString(IntentHelper.EXTRA_SUGGESTION_TYPE) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_suggestions, container, false)
        return mBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvCatalogObjects.addOnScrollListener(lazyScrollPageUploader)
        mBinding.rvCatalogObjects.layoutManager=LinearLayoutManager(activity)
        mBinding.rvCatalogObjects.addItemDecoration(DividerItemDecoration(activity, OrientationHelper.VERTICAL))
        mBinding.rvCatalogObjects.adapter=suggestionsAdapter

        showProgress(true)
        loadSuggestions(1)
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
        loadSuggestions(totalItems/ITEMS_ON_PAGE+1)
    }

    private fun loadSuggestions(page: Int)
    {
        getCompositeDisposable().add(ServiceGenerator.serverApi.getSuggestions(suggestionType, ITEMS_ON_PAGE, page)
                .compose(RxHelper.applySchedulers())
                .doOnSubscribe { lazyScrollPageUploader.setLoading(true) }
                .doFinally {
                    showProgress(false)
                    lazyScrollPageUploader.setLoading(false)
                    updateNoItemsView()
                }
                .subscribeWith(object : DisposableSingleObserver<Response<BaseResponseWithDataObject<Suggestion>>>() {
                    override fun onSuccess(result: Response<BaseResponseWithDataObject<Suggestion>>) {
                        if(result.isSuccessful&&result.body()?.code==200)
                        {
                            result.body()?.data?.items?.let {
                                val allItemsLoaded = it.size < ITEMS_ON_PAGE
                                suggestionsAdapter.isShowLoadingFooter = !allItemsLoaded
                                lazyScrollPageUploader.noMoreElements=allItemsLoaded

                                if(page==1)
                                    suggestionsAdapter.setData(it)
                                else
                                    suggestionsAdapter.addData(it)
                            }
                        }
                        else
                        {
                            UIHelper.showServerError(result, activity)
                            suggestionsAdapter.isShowLoadingFooter = false
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
