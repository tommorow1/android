package com.example.bloold.buildp.ui.fragments

import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bloold.buildp.R
import com.example.bloold.buildp.adapter.NameCodeAdapter
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.databinding.FragmentRecyclerViewBinding
import com.example.bloold.buildp.model.NameCodeInterface


class DocPubListFragment : Fragment() {
    private lateinit var mBinding:FragmentRecyclerViewBinding
    private lateinit var adapter:NameCodeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recycler_view, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, OrientationHelper.VERTICAL))
        mBinding.recyclerView.layoutManager=LinearLayoutManager(activity)
        adapter = NameCodeAdapter(OnItemClickListener {
            it.getSrcFile()?.let { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it))) }})
        arguments?.let { adapter.addAll((it.getParcelableArray(IntentHelper.EXTRA_DOC_PUB_MODEL_LIST) as Array<NameCodeInterface>).asList()) }
        mBinding.recyclerView.adapter=adapter
        updateNoItemsView()
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
    companion object {
        fun newInstance(items: Array<out Parcelable>?): DocPubListFragment {
            val fragment= DocPubListFragment()
            items?.let {
                Bundle().apply { putParcelableArray(IntentHelper.EXTRA_DOC_PUB_MODEL_LIST, items) }
                        .apply { fragment.arguments=this }
            }
            return fragment
        }
    }
}
