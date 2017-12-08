package com.example.bloold.buildp.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bloold.buildp.R
import com.example.bloold.buildp.components.BindingViewHolder
import com.example.bloold.buildp.databinding.ItemParticipantBinding
import com.example.bloold.buildp.model.Participant

class ParticipantAdapter:RecyclerView.Adapter<BindingViewHolder<ItemParticipantBinding>>() {

    var mValues: ArrayList<Participant> = ArrayList()

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int):BindingViewHolder<ItemParticipantBinding> {
        val listIemBinding = DataBindingUtil.inflate<ItemParticipantBinding>(LayoutInflater.from(parent.context), R.layout.item_participant, parent, false)
        return BindingViewHolder(listIemBinding)
    }

    override fun onBindViewHolder(holder:BindingViewHolder<ItemParticipantBinding>, position:Int) {
        mValues[holder.adapterPosition].let {
            holder.mLayoutBinding.tvName.text=String.format("${it.name} ${it.lastName}")
            Glide.with(holder.itemView.context)
                    .load(it.getFullAvatarUrl())
                    .apply(RequestOptions().circleCrop())
                    .into(holder.mLayoutBinding.ivAvatar)
        }
    }

    override fun getItemCount():Int = mValues.size

    fun addData(items: List<Participant>?) {
        items?.let {
            mValues.addAll(it)
            notifyItemRangeInserted(mValues.size-items.size, items.size)
        }
    }
}
