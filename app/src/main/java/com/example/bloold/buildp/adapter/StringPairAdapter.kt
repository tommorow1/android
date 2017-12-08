package com.example.bloold.buildp.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.bloold.buildp.R
import com.example.bloold.buildp.components.BindingViewHolder
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.databinding.ItemOneLineBinding
import java.util.*

/**
 * Created by sagus on 18.11.2017.
 */
class StringPairAdapter(private val onItemClickListener: OnItemClickListener<Pair<String,String>>?,
                        private var mData:ArrayList<Pair<String,String>>): RecyclerView.Adapter<BindingViewHolder<ItemOneLineBinding>>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ItemOneLineBinding> {
        val listIemBinding = DataBindingUtil.inflate<ItemOneLineBinding>(LayoutInflater.from(parent.context), R.layout.item_one_line, parent, false)
        return BindingViewHolder(listIemBinding)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ItemOneLineBinding>, position: Int) {
        val catalogObject = mData[position]
        holder.mLayoutBinding.tvName.text=catalogObject.first
        holder.itemView.setOnClickListener({onItemClickListener?.onItemClick(mData[holder.adapterPosition])})

    }

    override fun getItemCount(): Int =  mData.size
}
