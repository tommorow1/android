package com.example.bloold.buildp.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.bloold.buildp.MyApplication
import com.example.bloold.buildp.R
import com.example.bloold.buildp.components.BindingViewHolder
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.databinding.ItemAudioRemovableBinding
import com.example.bloold.buildp.databinding.ItemDocumentBinding
import com.example.bloold.buildp.model.AudioModel
import com.example.bloold.buildp.model.NameCodeInterface
import com.example.bloold.buildp.model.PhotoModel

class DocumentEditAdapter(private val onItemClickListener: OnItemClickListener<NameCodeInterface>?):RecyclerView.Adapter<BindingViewHolder<ItemDocumentBinding>>() {

    var mValues: ArrayList<NameCodeInterface> = ArrayList()
        private set
    var notUploadedDocs=HashSet<Int>()
        private set

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int):BindingViewHolder<ItemDocumentBinding> {
        val listIemBinding = DataBindingUtil.inflate<ItemDocumentBinding>(LayoutInflater.from(parent.context), R.layout.item_document, parent, false)
        return BindingViewHolder(listIemBinding)
    }

    override fun onBindViewHolder(holder:BindingViewHolder<ItemDocumentBinding>, position:Int) {
        holder.mLayoutBinding.tvName.text=mValues[holder.adapterPosition].getDocName()
        holder.itemView.setOnClickListener({onItemClickListener?.onItemClick(mValues[holder.adapterPosition])})

    }

    override fun getItemCount():Int = mValues.size

    fun clear() {
        val size = mValues.size
        mValues.clear()
        notifyItemRangeRemoved(0, size)
    }
    fun addData(item: NameCodeInterface, uploaded: Boolean=true) {
        if(!uploaded) notUploadedDocs.add(mValues.size)
        mValues.add(item)
        notifyItemInserted(mValues.size)
    }
    fun setDocUploaded(pos:Int)
    {
        notUploadedDocs.remove(pos)
    }
    fun addAll(items: List<NameCodeInterface>?) {
        items?.let {
            mValues.addAll(it)
            notifyItemRangeInserted(mValues.size-items.size, items.size)
        }
    }
}
