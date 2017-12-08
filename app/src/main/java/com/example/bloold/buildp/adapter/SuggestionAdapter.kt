package com.example.bloold.buildp.adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.bloold.buildp.R
import com.example.bloold.buildp.common.BindingHelper
import com.example.bloold.buildp.components.BindingViewHolder
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.databinding.ItemLoadingBinding
import com.example.bloold.buildp.databinding.ItemSuggestionBinding
import com.example.bloold.buildp.model.Suggestion
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by sagus on 18.11.2017.
 */
class SuggestionAdapter(private val onItemClickListener: OnItemClickListener<Suggestion>?): RecyclerView.Adapter<BindingViewHolder<out ViewDataBinding>>()
{
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
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
    val mData = ArrayList<Suggestion>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<out ViewDataBinding> {
        return if (viewType == TYPE_FOOTER) {
            val listIemBinding = DataBindingUtil.inflate<ItemLoadingBinding>(LayoutInflater.from(parent.context), R.layout.item_loading, parent, false)
            BindingViewHolder(listIemBinding)
        } else {
            val listIemBinding = DataBindingUtil.inflate< ItemSuggestionBinding>(LayoutInflater.from(parent.context), R.layout.item_suggestion, parent, false)
            BindingViewHolder(listIemBinding)
        }
    }

    override fun onBindViewHolder(holderRaw: BindingViewHolder<out ViewDataBinding>, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == TYPE_DATA) {
            val holder = holderRaw as BindingViewHolder<ItemSuggestionBinding>
            val suggestion = mData[position]

            holder.mLayoutBinding.tvObjectName.text=suggestion.objectName
            holder.mLayoutBinding.tvDate.text=dateFormat.format(suggestion.dateCreate)
            BindingHelper.configureSuggestionStatusBadge(suggestion, holder.mLayoutBinding.llStatus,
                    holder.mLayoutBinding.ivStatus, holder.mLayoutBinding.tvStatus)
            holder.itemView.setOnClickListener({onItemClickListener?.onItemClick(mData[holder.adapterPosition])})
            if(suggestion.diffList?.isNotEmpty()==true)
            {
                holder.mLayoutBinding.tvName.text=suggestion.diffList?.firstOrNull()?.title
                holder.mLayoutBinding.tvName.visibility= View.VISIBLE
            }
            else holder.mLayoutBinding.tvName.visibility= View.GONE
        }
    }

    override fun getItemViewType(position: Int): Int =
            if (isShowLoadingFooter && position == mData.size) TYPE_FOOTER else TYPE_DATA

    override fun getItemCount(): Int {
        var size = mData.size
        if (isShowLoadingFooter) size += 1
        return size
    }

    fun setData(newData: Array<Suggestion>?) {
        mData.clear()
        if (newData != null) mData.addAll(newData)
        notifyDataSetChanged()
    }

    fun addData(newData: Array<Suggestion>?) {
        if (newData == null) return

        val positionOfNewItem = mData.size
        //if(showLoadingFooter) positionOfNewItem-=1;
        mData.addAll(newData)
        notifyItemRangeInserted(positionOfNewItem, newData.size)
    }

    companion object {
        private val TYPE_DATA = 0
        private val TYPE_FOOTER = 1
    }
}
