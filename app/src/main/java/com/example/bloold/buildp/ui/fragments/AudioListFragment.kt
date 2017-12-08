package com.example.bloold.buildp.ui.fragments

import android.databinding.DataBindingUtil
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bloold.buildp.R
import com.example.bloold.buildp.adapter.AudioEditAdapter
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.databinding.FragmentRecyclerViewBinding
import com.example.bloold.buildp.model.AudioModel


class AudioListFragment : Fragment() {
    private lateinit var mBinding:FragmentRecyclerViewBinding
    private lateinit var adapter:AudioEditAdapter
    private var player:MediaPlayer=MediaPlayer().apply { setAudioStreamType(AudioManager.STREAM_MUSIC) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recycler_view, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, OrientationHelper.VERTICAL))
        mBinding.recyclerView.layoutManager=LinearLayoutManager(activity)
        player.setOnPreparedListener { it.start() }
        adapter = AudioEditAdapter(OnItemClickListener {
            adapter.getPlayAudioModelNow()?.let { item ->
                if(item==it)
                {
                    adapter.stopPlay()
                    player.reset()
                    return@OnItemClickListener
                }
            }
            try {
                adapter.startPlay(it)
                if(player.isPlaying)
                    player.reset()
                player.setDataSource(it.fullPath())
                player.prepareAsync()
                //player.start()
            } catch (e: Exception) {
                adapter.stopPlay()
                e.printStackTrace()
            }
        })
        arguments?.let { adapter.addAll((it.getParcelableArray(IntentHelper.EXTRA_AUDIO_MODEL_LIST) as Array<AudioModel>).asList()) }
        mBinding.recyclerView.adapter=adapter
        updateNoItemsView()
    }

    override fun onPause() {
        if(player.isPlaying)
        {
            adapter.stopPlay()
            player.reset()
        }
        super.onPause()
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
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
        fun newInstance(items: Array<AudioModel>?): AudioListFragment {
            val fragment= AudioListFragment()
            items?.let {
                Bundle().apply { putParcelableArray(IntentHelper.EXTRA_AUDIO_MODEL_LIST, items) }
                        .apply { fragment.arguments=this }
            }
            return fragment
        }
    }
}
