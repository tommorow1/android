package com.example.bloold.buildp.catalog.`object`

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.bloold.buildp.R
import com.example.bloold.buildp.model.CatalogObjectsModel

interface AdapterListener {
    fun onObjectClicked(item: CatalogObjectsModel)
}

class AdapterCatalogObject(private val listener: AdapterListener):RecyclerView.Adapter<AdapterCatalogObject.ViewHolder>() {

    private var mValues: ArrayList<CatalogObjectsModel> = ArrayList<CatalogObjectsModel>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_catalog_object, parent, false)

        context = parent.context

        return ViewHolder(view)
    }

    fun getValues(): ArrayList<CatalogObjectsModel>{
        return mValues
    }

    override fun onBindViewHolder(holder: ViewHolder, position:Int) {
        holder.tvName.setText(mValues.get(position).name)

        Glide.with(context)
                .load(mValues.get(position).src)
                .into(holder.ivBuild)

        holder.mView.setOnClickListener(object:View.OnClickListener {
            override fun onClick(v:View) {
                listener.onObjectClicked(mValues.get(position))
            }
        })
    }

    override fun getItemCount():Int {
        return mValues?.size ?: 0
    }

    inner class ViewHolder( val mView:View):RecyclerView.ViewHolder(mView) {
        val ivBuild: ImageView
        val ivLocation: ImageView
      //  val ivArrow: ImageView
        val tvName: TextView

        init{
            ivBuild = mView.findViewById<ImageView>(R.id.ivBuild)
            ivLocation = mView.findViewById<ImageView>(R.id.ivLocation)
            //ivArrow = mView.findViewById<ImageView>(R.id.ivArrow)
            tvName = mView.findViewById<TextView>(R.id.name)
        }

        override fun toString():String {
            return super.toString() + " '" + tvName.text + "'"
        }
    }

    fun replaceAll(items: List<CatalogObjectsModel>) {
        clear()
        addAll(items, 0)
    }

    fun clear() {
        val size = mValues.size
        mValues.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun addAll(items: List<CatalogObjectsModel>?, startPosition: Int) {
        if (items == null || items.size == 0) {
            return
        }

        mValues.addAll(startPosition, items)

        notifyDataSetChanged()
    }

    fun addAll(items: List<CatalogObjectsModel>?) {
        addAll(items, mValues.size)
        Log.d("adapter", mValues.size.toString())
    }
}
