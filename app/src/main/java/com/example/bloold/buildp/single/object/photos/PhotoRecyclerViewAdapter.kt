package com.example.bloold.buildp.single.`object`.photos

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.bloold.buildp.R
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.model.PhotoModel

class PhotoRecyclerViewAdapter(private val mListener:OnItemClickListener<PhotoModel>?,
                                private val context: Context):
                                RecyclerView.Adapter<PhotoRecyclerViewAdapter.ViewHolder>() {

    var mValues: ArrayList<PhotoModel> = ArrayList()

    companion object {
     private val TAG = "PhotoRecyclerViewAdapter"
    }

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int):ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder:ViewHolder, position:Int) {

        Glide.with(context)
            .load(mValues.get(position).fullPath())
            .into(holder.mIvPhoto)

        holder.mView.setOnClickListener { mListener?.onItemClick(mValues[holder.adapterPosition]) }
    }

    override fun getItemCount():Int = mValues.size

    fun replaceAll(items: ArrayList<PhotoModel>) {
        clear()
        addAll(items, 0)
    }

    fun clear() {
        val size = mValues.size
        mValues.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun addAll(items: List<PhotoModel>?, startPosition: Int) {
        items?.let {
            mValues.addAll(startPosition, it)
            notifyDataSetChanged()
        }
    }

    fun addAll(items: List<PhotoModel>?) {
        addAll(items, mValues.size)
        Log.d("adapter", mValues.size.toString())
    }

    inner class ViewHolder( val mView:View):RecyclerView.ViewHolder(mView) {
        val mIvPhoto: ImageView = mView.findViewById(R.id.ivPhoto)

        override fun toString():String = super.toString() + "$TAG, ${mValues.size}"
    }
}
