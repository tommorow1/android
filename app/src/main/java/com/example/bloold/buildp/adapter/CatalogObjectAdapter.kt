package com.example.bloold.buildp.adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.bloold.buildp.R
import com.example.bloold.buildp.api.data.CatalogObject
import com.example.bloold.buildp.components.BindingViewHolder
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.databinding.ItemCatalogObjectBinding
import com.example.bloold.buildp.databinding.ItemLoadingBinding
import java.util.*

/**
 * Created by sagus on 18.11.2017.
 */
class CatalogObjectAdapter(private val onItemClickListener: OnItemClickListener<CatalogObject>?,
                           private val onMapMarkerClickListener: OnItemClickListener<CatalogObject>?): RecyclerView.Adapter<BindingViewHolder<out ViewDataBinding>>()
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
    val mData = ArrayList<CatalogObject>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<out ViewDataBinding> {
        return if (viewType == TYPE_FOOTER) {
            val listIemBinding = DataBindingUtil.inflate<ItemLoadingBinding>(LayoutInflater.from(parent.context), R.layout.item_loading, parent, false)
            BindingViewHolder(listIemBinding)
        } else {
            val listIemBinding = DataBindingUtil.inflate<ItemCatalogObjectBinding>(LayoutInflater.from(parent.context), R.layout.item_catalog_object, parent, false)
            BindingViewHolder(listIemBinding)
        }
    }

    override fun onBindViewHolder(holderRaw: BindingViewHolder<out ViewDataBinding>, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == TYPE_DATA) {
            val holder = holderRaw as BindingViewHolder<ItemCatalogObjectBinding>
            val catalogObject = mData[position]

            holder.mLayoutBinding.name.text=catalogObject.name
            Glide.with(holderRaw.itemView.context)
                    .load(catalogObject.detailPicture?.fullPath())
                    .into(holder.mLayoutBinding.ivBuild)

            holder.mLayoutBinding.ivLocation.setOnClickListener({onMapMarkerClickListener?.onItemClick(mData[holder.adapterPosition])})
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

    fun setData(newData: Array<CatalogObject>?) {
        mData.clear()
        if (newData != null) mData.addAll(newData)
        notifyDataSetChanged()
    }

    fun addData(newData: Array<CatalogObject>?) {
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
