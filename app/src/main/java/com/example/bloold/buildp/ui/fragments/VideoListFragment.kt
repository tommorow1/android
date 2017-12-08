package com.example.bloold.buildp.ui.fragments

import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bloold.buildp.R
import com.example.bloold.buildp.adapter.VideoEditAdapter
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.databinding.FragmentRecyclerViewBinding
import com.example.bloold.buildp.model.VideoModel


class VideoListFragment : Fragment() {
    private lateinit var mBinding:FragmentRecyclerViewBinding
    private lateinit var adapter:VideoEditAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recycler_view, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.recyclerView.layoutManager=LinearLayoutManager(activity)
        adapter = VideoEditAdapter(OnItemClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.getYoutubeLink())))
        }, null)
        arguments?.let { adapter.addAll((it.getParcelableArray(IntentHelper.EXTRA_VIDEO_MODEL_LIST) as Array<VideoModel>).asList()) }
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
        fun newInstance(items: Array<VideoModel>?): VideoListFragment {
            val fragment= VideoListFragment()
            items?.let {
                Bundle().apply { putParcelableArray(IntentHelper.EXTRA_VIDEO_MODEL_LIST, items) }
                        .apply { fragment.arguments=this }
            }
            return fragment
        }
    }
}
