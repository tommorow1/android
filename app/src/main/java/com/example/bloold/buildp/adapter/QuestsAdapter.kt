package com.example.bloold.buildp.adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bloold.buildp.R
import com.example.bloold.buildp.components.BindingViewHolder
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.databinding.ItemLoadingBinding
import com.example.bloold.buildp.databinding.ItemQuestBinding
import com.example.bloold.buildp.model.Quest
import java.util.*

/**
 * Created by sagus on 18.11.2017.
 */
class QuestsAdapter(private val onItemClickListener: OnItemClickListener<Quest>?) : RecyclerView.Adapter<BindingViewHolder<out ViewDataBinding>>()
{
    var onParticipateClickListener: OnItemClickListener<Quest>?=null
    var isShowLoadingFooter = false
        set(showLoadingFooter) {
            val wasShowed = this.isShowLoadingFooter
            field = showLoadingFooter
            if (wasShowed != showLoadingFooter) {
                if (!showLoadingFooter)
                    notifyItemRemoved(mData.size)
                else
                    notifyItemInserted(mData.size)
            }
        }
    private val mData = ArrayList<Quest>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<out ViewDataBinding> {
        return if (viewType == TYPE_FOOTER) {
            val listIemBinding = DataBindingUtil.inflate<ItemLoadingBinding>(LayoutInflater.from(parent.context), R.layout.item_loading, parent, false)
            BindingViewHolder(listIemBinding)
        } else {
            val listIemBinding = DataBindingUtil.inflate<ItemQuestBinding>(LayoutInflater.from(parent.context), R.layout.item_quest, parent, false)
            BindingViewHolder(listIemBinding)
        }
    }

    override fun onBindViewHolder(holderRaw: BindingViewHolder<out ViewDataBinding>, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == TYPE_DATA) {
            val holder = holderRaw as BindingViewHolder<ItemQuestBinding>
            val item = mData[position]

            holder.mLayoutBinding.tvBonuses.text=item.points.toInt().toString()
            holder.mLayoutBinding.tvParticipants.text=item.participants.toInt().toString()
            holder.mLayoutBinding.tvName.text=item.name
            holder.mLayoutBinding.tvDescription.text=item.previewText

            if(onParticipateClickListener!=null)
            {
                holder.mLayoutBinding.bAction.text=holderRaw.itemView.context.getString(if(item.isParticipate) R.string.cancel_participate else R.string.participate)
                holder.mLayoutBinding.bAction.visibility= View.VISIBLE
                holder.mLayoutBinding.bAction.setOnClickListener { onParticipateClickListener?.onItemClick(mData[holder.adapterPosition]) }
            }
            else
                holder.mLayoutBinding.bAction.visibility= View.INVISIBLE

            //TODO менять название на кнопке и обрабатывать нажатие на кнопку

            Glide.with(holderRaw.itemView.context)
                    .load(item.pictureDetail.fullPath())
                    .apply(RequestOptions().centerCrop())
                    .into(holder.mLayoutBinding.imageView)
            holder.itemView.setOnClickListener({onItemClickListener?.onItemClick(mData[holder.adapterPosition])})
        }
    }

    override fun getItemViewType(position: Int): Int =
            if (isShowLoadingFooter && position == mData.size) TYPE_FOOTER else TYPE_DATA

    override fun getItemCount(): Int {
        var size = mData.size
        if (isShowLoadingFooter) size += 1
        return size
    }
    fun participateToQuest(quest: Quest, participate:Boolean)
    {
        val pos=mData.indexOf(quest)
        if(pos!=-1) {
            if(participate)
                quest.participants += 1
            else quest.participants -= 1
            quest.isParticipate = participate
            notifyItemChanged(pos)
        }
    }

    fun setData(newData: Array<Quest>?) {
        mData.clear()
        if (newData != null) mData.addAll(newData)
        notifyDataSetChanged()
    }

    fun addData(newData: Array<Quest>?) {
        if (newData == null) return

        val positionOfNewItem = mData.size
        mData.addAll(newData)
        notifyItemRangeInserted(positionOfNewItem, newData.size)
    }

    companion object {
        private val TYPE_DATA = 0
        private val TYPE_FOOTER = 1
    }
}
