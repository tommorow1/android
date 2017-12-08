package com.example.bloold.buildp.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.bloold.buildp.R
import com.example.bloold.buildp.components.BindingViewHolder
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.components.UIHelper
import com.example.bloold.buildp.databinding.ItemVideoRemovableBinding
import com.example.bloold.buildp.model.AudioModel
import com.example.bloold.buildp.model.VideoModel

class VideoEditAdapter(private val onItemClickListener:OnItemClickListener<VideoModel>?,
                       private val deleteClickListener:OnItemClickListener<VideoModel>?):
                                RecyclerView.Adapter<BindingViewHolder<ItemVideoRemovableBinding>>() {

    var mValues: ArrayList<VideoModel> = ArrayList()

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int):BindingViewHolder<ItemVideoRemovableBinding> {
        val listIemBinding = DataBindingUtil.inflate<ItemVideoRemovableBinding>(LayoutInflater.from(parent.context), R.layout.item_video_removable, parent, false)
        return BindingViewHolder(listIemBinding)
    }

    override fun onBindViewHolder(holder:BindingViewHolder<ItemVideoRemovableBinding>, position:Int)
    {
        Glide.with(holder.itemView.context)
            .load(UIHelper.getYoutubePreviewImage(mValues[position].getYoutubeLink())?:"")
            .into(holder.mLayoutBinding.ivPhoto)

        holder.itemView.setOnClickListener { onItemClickListener?.onItemClick(mValues[holder.adapterPosition]) }
        holder.mLayoutBinding.ivRemove.setOnClickListener { deleteClickListener?.onItemClick(mValues[holder.adapterPosition]) }
    }

    override fun getItemCount():Int = mValues.size

    fun remove(item: VideoModel) {
        val index=mValues.indexOf(item)
        if(index!=-1)
        {
            mValues.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun clear() {
        val size = mValues.size
        mValues.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun addData(item: VideoModel) {
        mValues.add(item)
        notifyItemInserted(mValues.size)
    }
    fun addAll(items: List<VideoModel>?) {
        items?.let {
            mValues.addAll(it)
            notifyItemRangeInserted(mValues.size-items.size, items.size)
        }
    }
}
