package com.example.bloold.buildp.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bloold.buildp.R
import com.example.bloold.buildp.api.data.CatalogObject
import com.example.bloold.buildp.components.BindingViewHolder
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.databinding.ItemPublicationsBinding
import com.example.bloold.buildp.model.NameCodeInterface

class NameCodeAdapter(private val onItemClickListener: OnItemClickListener<NameCodeInterface>?):RecyclerView.Adapter<BindingViewHolder<ItemPublicationsBinding>>() {

    var mValues: ArrayList<NameCodeInterface> = ArrayList()

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int):BindingViewHolder<ItemPublicationsBinding> {
        val listIemBinding = DataBindingUtil.inflate<ItemPublicationsBinding>(LayoutInflater.from(parent.context), R.layout.item_publications, parent, false)
        return BindingViewHolder(listIemBinding)
    }

    override fun onBindViewHolder(holder:BindingViewHolder<ItemPublicationsBinding>, position:Int) {
        val item=mValues[holder.adapterPosition]
        holder.mLayoutBinding.tvName.text=item.getDocName()
        holder.mLayoutBinding.flCodeBlock.visibility=if(item.getDocCode().isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.mLayoutBinding.tvDocumentCode.text=item.getDocCode()
        holder.itemView.setOnClickListener({onItemClickListener?.onItemClick(mValues[holder.adapterPosition])})
    }

    override fun getItemCount():Int = mValues.size

    fun clear() {
        val size = mValues.size
        mValues.clear()
        notifyItemRangeRemoved(0, size)
    }
    fun addAll(items: List<NameCodeInterface>?) {
        items?.let {
            mValues.addAll(it)
            notifyItemRangeInserted(mValues.size-items.size, items.size)
        }
    }
}
