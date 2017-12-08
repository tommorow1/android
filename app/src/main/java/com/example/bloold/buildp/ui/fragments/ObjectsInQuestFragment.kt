package com.example.bloold.buildp.ui.fragments

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bloold.buildp.R
import com.example.bloold.buildp.adapter.CatalogObjectAdapter
import com.example.bloold.buildp.api.data.CatalogObject
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.databinding.FragmentObjectsInQuestBinding
import com.example.bloold.buildp.ui.CatalogObjectDetailsActivity
import com.example.bloold.buildp.ui.MapActivity

class ObjectsInQuestFragment : Fragment()
{
    private lateinit var mBinding: FragmentObjectsInQuestBinding
    private lateinit var catalogObjectAdapter: CatalogObjectAdapter

    companion object {
        fun newInstance(items: ArrayList<CatalogObject>?): ObjectsInQuestFragment {
            return ObjectsInQuestFragment()
                    .apply { arguments = Bundle().apply { putParcelableArrayList(IntentHelper.EXTRA_OBJECT_LIST, items) }}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catalogObjectAdapter = CatalogObjectAdapter(OnItemClickListener {
                    startActivity(Intent(context, CatalogObjectDetailsActivity::class.java)
                        .putExtra(IntentHelper.EXTRA_OBJECT_ID, it.id))
                },
                OnItemClickListener {
                    if(it.getLocation()!=null)
                        activity?.let { act-> MapActivity.launch(act, it) }
                })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_objects_in_quest, container, false)
        return mBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        catalogObjectAdapter.setData(arguments?.getParcelableArrayList<CatalogObject>(IntentHelper.EXTRA_OBJECT_LIST)?.toTypedArray())
        mBinding.rvCatalogObjects.layoutManager=LinearLayoutManager(activity)
        mBinding.rvCatalogObjects.adapter=catalogObjectAdapter
        updateNoItemsView()
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
}
