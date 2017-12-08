package com.example.bloold.buildp.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.bloold.buildp.R
import com.example.bloold.buildp.components.BindingViewHolder
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.databinding.ItemPhotoRemovableBinding
import com.example.bloold.buildp.model.PhotoModel

class PhotoEditAdapter(private val onItemClickListener:OnItemClickListener<PhotoModel>?,
                       private val deleteClickListener:OnItemClickListener<PhotoModel>?):
                                RecyclerView.Adapter<BindingViewHolder<ItemPhotoRemovableBinding>>() {

    var mValues: ArrayList<PhotoModel> = ArrayList()

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int):BindingViewHolder<ItemPhotoRemovableBinding> {
        val listIemBinding = DataBindingUtil.inflate<ItemPhotoRemovableBinding>(LayoutInflater.from(parent.context), R.layout.item_photo_removable, parent, false)
        return BindingViewHolder(listIemBinding)
    }

    override fun onBindViewHolder(holder:BindingViewHolder<ItemPhotoRemovableBinding>, position:Int) {
        val photoModel=mValues[position]
        //Отображаем либо из сети, либо локальный(если ещё не загружен и нет id)
        Glide.with(holder.itemView.context)
            .load(if(photoModel.id==-1L) photoModel.src else photoModel.fullPath())
            .into(holder.mLayoutBinding.ivPhoto)

        holder.itemView.setOnClickListener { onItemClickListener?.onItemClick(mValues[holder.adapterPosition]) }
        holder.mLayoutBinding.ivRemove.setOnClickListener { deleteClickListener?.onItemClick(mValues[holder.adapterPosition]) }
    }

    override fun getItemCount():Int = mValues.size

    fun remove(item: PhotoModel) {
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

    fun setIdForUploadedPhoto(filename:String, newKey:Long)
    {
        var size = mValues.size
        mValues.firstOrNull { it.src==filename&&it.id==-1L }?.id=newKey
        size = mValues.size
    }

    fun addData(item: PhotoModel) {
        mValues.add(item)
        notifyItemInserted(mValues.size)
    }
    fun addAll(items: List<PhotoModel>?) {
        items?.let {
            mValues.addAll(it)
            notifyItemRangeInserted(mValues.size-items.size, items.size)
        }
    }
}
