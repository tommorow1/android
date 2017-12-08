package com.example.bloold.buildp.sort.fragment

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.example.bloold.buildp.R
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.model.Category

class SortAdapter(private val mValues: List<Category>,
                  private val customRes: Int,
                  private val listener: OnItemClickListener<Category>)
    :RecyclerView.Adapter<SortAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(customRes,
                    //R.layout.item_filter_start,
                    parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position:Int) {
        holder.mItem = mValues[position]
        holder.mCheckText.text = mValues[position].name

        holder.mView.setOnClickListener( {
            var item = mValues[position]
            listener.onItemClick(item) } )
    }

    override fun getItemCount():Int {
        return mValues.size
    }

    inner class ViewHolder( val mView:View):RecyclerView.ViewHolder(mView) {
        val mCheck:CheckBox? = mView.findViewById(R.id.chbFilter)
        val mCheckText:TextView = mView.findViewById(R.id.tvName)
        var mItem: Category? = null
    }
}
