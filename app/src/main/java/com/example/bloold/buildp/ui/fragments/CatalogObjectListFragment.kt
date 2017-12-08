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
import com.example.bloold.buildp.adapter.CatalogObjectAdapter
import com.example.bloold.buildp.api.ApiHelper
import com.example.bloold.buildp.api.ServiceGenerator
import com.example.bloold.buildp.api.data.BaseResponseWithDataObject
import com.example.bloold.buildp.api.data.CatalogObject
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.common.RxHelper
import com.example.bloold.buildp.common.Settings
import com.example.bloold.buildp.components.EventFragment
import com.example.bloold.buildp.components.LazyScrollPageUploader
import com.example.bloold.buildp.components.NetworkFragment
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.databinding.FragmentCatalogObjectBinding
import com.example.bloold.buildp.model.CatalogObjectsModel
import com.example.bloold.buildp.model.Category
import com.example.bloold.buildp.model.SortObject
import com.example.bloold.buildp.services.NetworkIntentService
import com.example.bloold.buildp.ui.CatalogObjectDetailsActivity
import com.example.bloold.buildp.ui.MainActivity
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.android.synthetic.main.activity_catalog_object_details.*
import java.net.ConnectException
import java.net.UnknownHostException

class CatalogObjectListFragment : NetworkFragment(), OnItemClickListener<CatalogObject>, LazyScrollPageUploader.OnLazyScrollUploaderListener {
    private lateinit var mBinding: FragmentCatalogObjectBinding
    private lateinit var catalogObjectAdapter: CatalogObjectAdapter
    private var lazyScrollPageUploader = LazyScrollPageUploader(this)
    private var objectsArray: ArrayList<CatalogObjectsModel> = ArrayList()
    private var isHaveCatalog: Boolean = false
    var category: Category? = null
    private var queryString:String?=null
    private var queryType:String?=null

    companion object {
        private val ITEMS_ON_PAGE = 5
        private val KEY_RESPONSE_ARRAY_OBJECTS = "catalog_response"
        private val KEY_RESPONSE_HAVE_CATALOG = "catalog"
        private val KEY_RESPONSE_SORTED_OBJECTS = "sorted"

        /*fun newInstance(): CatalogObjectListFragment = CatalogObjectListFragment()
                .apply { arguments = Bundle().apply { putInt(IntentHelper.EXTRA_CATEGORY_ID, categoryId) } }
*/
/*
        fun newInstance(items: ArrayList<String>): CatalogObjectListFragment {
            return CatalogObjectListFragment()
                    .apply { arguments = Bundle().apply { putStringArrayList(KEY_RESPONSE, items) } }
        }
*/

        fun newInstance(items: ArrayList<CatalogObjectsModel>, isHave: Boolean = true): CatalogObjectListFragment {
            return CatalogObjectListFragment()
                    .apply { arguments = Bundle().apply { putParcelableArrayList(KEY_RESPONSE_ARRAY_OBJECTS, items)
                    putBoolean(KEY_RESPONSE_HAVE_CATALOG, isHave)} }
        }

        fun newInstance(category: Category?, queryType:String?=null, queryString:String?=null): CatalogObjectListFragment {
            return CatalogObjectListFragment()
                    .apply { arguments = Bundle().apply {
                        putParcelable(KEY_RESPONSE_SORTED_OBJECTS, category)
                        if(queryString!=null&&queryType!=null)
                        {
                            putString(IntentHelper.EXTRA_QUERY_STRING, queryString)
                            putString(IntentHelper.EXTRA_QUERY_TYPE, queryType)
                        }
                    } }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catalogObjectAdapter = CatalogObjectAdapter(this,
                OnItemClickListener {
                    if(it.getLocation()!=null)
                        (activity as? MainActivity)?.showMap(it)
                })
        queryString=arguments?.getString(IntentHelper.EXTRA_QUERY_STRING)
        queryType=arguments?.getString(IntentHelper.EXTRA_QUERY_TYPE)
        arguments?.let {
            if (it.containsKey(KEY_RESPONSE_ARRAY_OBJECTS)) {
                objectsArray = it.getParcelableArrayList(KEY_RESPONSE_ARRAY_OBJECTS)
                isHaveCatalog = it.getBoolean(KEY_RESPONSE_HAVE_CATALOG)
            }
            if(it.containsKey(KEY_RESPONSE_SORTED_OBJECTS)){
                category = it.getParcelable(KEY_RESPONSE_SORTED_OBJECTS)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.toolbar?.title=getString(R.string.navigation_drawer_catalog_object)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_catalog_object, container, false)
        return mBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvCatalogObjects.addOnScrollListener(lazyScrollPageUploader)
        mBinding.rvCatalogObjects.layoutManager=LinearLayoutManager(activity)
        mBinding.rvCatalogObjects.adapter=catalogObjectAdapter

        showProgress(true)
        loadCatalogObjects(1)
        /*if (view is RecyclerView) {
            if(!isHaveCatalog) {
                if(urlResponse != null)
                    presenter.getCatalogObjects(urlResponse!!)
            } else
                onObjectsLoaded(objectsArray)
            if(sortedObject != null){

            }
        }*/
    }
    fun refreshCatalogList()
    {
        loadCatalogObjects(1)
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


    override fun onItemClick(item: CatalogObject) {
        startActivity(Intent(context, CatalogObjectDetailsActivity::class.java)
                .putExtra(IntentHelper.EXTRA_OBJECT_ID, item.id))
    }

    override fun onLoadData(totalItems: Int) {
        loadCatalogObjects(totalItems/ITEMS_ON_PAGE+1)
    }

    private fun loadCatalogObjects(page: Int)
    {
        val filters = HashMap<String,String>()
        Settings.catalogFilters?.let {
            for(i in 0 until it.size) {
                filters.put("filter[IBLOCK_SECTION_ID][$i]", it.elementAt(i).toString())
            }
        }
        category?.id?.let { filters.put("filter[IBLOCK_SECTION_ID][${filters.size}]",it) }
        getCompositeDisposable().add(ServiceGenerator.serverApi.getCatalogObjects(filters, ITEMS_ON_PAGE, page, category?.id,
                searchQuery = ApiHelper.generateSearchParams(queryType, queryString))
                .compose(RxHelper.applySchedulers())
                .doOnSubscribe { lazyScrollPageUploader.setLoading(true) }
                .doFinally {
                    showProgress(false)
                    lazyScrollPageUploader.setLoading(false)
                    updateNoItemsView()
                }
                .subscribeWith(object : DisposableSingleObserver<BaseResponseWithDataObject<CatalogObject>>() {
                    override fun onSuccess(result: BaseResponseWithDataObject<CatalogObject>) {
                        result.data?.items?.let {
                            val allItemsLoaded = it.size < ITEMS_ON_PAGE
                            catalogObjectAdapter.isShowLoadingFooter = !allItemsLoaded
                            lazyScrollPageUploader.noMoreElements=allItemsLoaded

                            if(page==1)
                                catalogObjectAdapter.setData(it)
                            else
                                catalogObjectAdapter.addData(it)
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
