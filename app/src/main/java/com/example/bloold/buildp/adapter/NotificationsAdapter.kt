package com.example.bloold.buildp.adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bloold.buildp.R
import com.example.bloold.buildp.components.BindingViewHolder
import com.example.bloold.buildp.databinding.ItemLoadingBinding
import com.example.bloold.buildp.databinding.ItemNotificationBinding
import com.example.bloold.buildp.model.NotificationInfo
import java.util.*

/**
 * Created by sagus on 18.11.2017.
 */
class NotificationsAdapter: RecyclerView.Adapter<BindingViewHolder<out ViewDataBinding>>()
{
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
    private val mData = ArrayList<NotificationInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<out ViewDataBinding> {
        return if (viewType == TYPE_FOOTER) {
            val listIemBinding = DataBindingUtil.inflate<ItemLoadingBinding>(LayoutInflater.from(parent.context), R.layout.item_loading, parent, false)
            BindingViewHolder(listIemBinding)
        } else {
            val listIemBinding = DataBindingUtil.inflate<ItemNotificationBinding>(LayoutInflater.from(parent.context), R.layout.item_notification, parent, false)
            BindingViewHolder(listIemBinding)
        }
    }

    override fun onBindViewHolder(holderRaw: BindingViewHolder<out ViewDataBinding>, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == TYPE_DATA) {
            val holder = holderRaw as BindingViewHolder<ItemNotificationBinding>
            val item = mData[position]

            holder.mLayoutBinding.tvMessage.text=item.noticeData.message
            holder.mLayoutBinding.tvDate.text=item.dateCreate

            if(item.noticeData.getFullUserFromAvatarUrl()!=null) {
                Glide.with(holderRaw.itemView.context)
                        .load(item.noticeData.getFullUserFromAvatarUrl())
                        .apply(RequestOptions().circleCrop())
                        .into(holder.mLayoutBinding.imageView)
            }
            else
            {
                var imageToShow = when(item.noticeData.type) {
                    2-> R.drawable.ic_notification_accepted
                    3-> R.drawable.ic_notification_declined
                    else -> R.drawable.ic_notification_accepted
                }
                holder.mLayoutBinding.imageView.setImageResource(imageToShow)
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
            if (isShowLoadingFooter && position == mData.size) TYPE_FOOTER else TYPE_DATA

    override fun getItemCount(): Int {
        var size = mData.size
        if (isShowLoadingFooter) size += 1
        return size
    }

    fun setData(newData: Array<NotificationInfo>?) {
        mData.clear()
        if (newData != null) mData.addAll(newData)
        notifyDataSetChanged()
    }

    fun addData(newData: Array<NotificationInfo>?) {
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
