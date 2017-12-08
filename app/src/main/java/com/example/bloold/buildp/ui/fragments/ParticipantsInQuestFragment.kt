package com.example.bloold.buildp.ui.fragments

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bloold.buildp.R
import com.example.bloold.buildp.adapter.ParticipantAdapter
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.databinding.FragmentParticipantsInQuestBinding
import com.example.bloold.buildp.model.Participant

class ParticipantsInQuestFragment : Fragment()
{
    private lateinit var mBinding: FragmentParticipantsInQuestBinding
    private lateinit var participantAdapter: ParticipantAdapter

    companion object {
        fun newInstance(items: ArrayList<Participant>?): ParticipantsInQuestFragment {
            return ParticipantsInQuestFragment()
                    .apply { arguments = Bundle().apply { putParcelableArrayList(IntentHelper.EXTRA_PARTICIPANT_LIST, items) }}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        participantAdapter = ParticipantAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_participants_in_quest, container, false)
        return mBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        participantAdapter.addData(arguments?.getParcelableArrayList(IntentHelper.EXTRA_PARTICIPANT_LIST))
        mBinding.recyclerView.layoutManager=LinearLayoutManager(activity)
        mBinding.recyclerView.adapter=participantAdapter
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
}
