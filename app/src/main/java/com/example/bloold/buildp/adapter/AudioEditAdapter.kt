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
import com.example.bloold.buildp.model.AudioModel
import com.example.bloold.buildp.model.PhotoModel

class AudioEditAdapter(private val onItemClickListener:OnItemClickListener<AudioModel>?,
                       private val deleteClickListener:OnItemClickListener<AudioModel>?=null):
                                RecyclerView.Adapter<BindingViewHolder<ItemAudioRemovableBinding>>() {

    var mValues: ArrayList<AudioModel> = ArrayList()
    private var itemPosPlaybackNow=-1

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int):BindingViewHolder<ItemAudioRemovableBinding> {
        val listIemBinding = DataBindingUtil.inflate<ItemAudioRemovableBinding>(LayoutInflater.from(parent.context), R.layout.item_audio_removable, parent, false)
        // Для режима отображения ставим отступ больше
        if(deleteClickListener==null) {
            val padding=MyApplication.instance.resources.getDimension(R.dimen.activity_vertical_margin).toInt()
            listIemBinding.root.setPadding(padding, padding, padding, padding)
        }
        return BindingViewHolder(listIemBinding)
    }

    override fun onBindViewHolder(holder:BindingViewHolder<ItemAudioRemovableBinding>, position:Int) {
        holder.mLayoutBinding.tvName.text=mValues[holder.adapterPosition].name
        holder.itemView.setOnClickListener { onItemClickListener?.onItemClick(mValues[holder.adapterPosition]) }
        holder.mLayoutBinding.ivRemove.setOnClickListener { deleteClickListener?.onItemClick(mValues[holder.adapterPosition]) }
        holder.mLayoutBinding.ivPlay.setImageResource(if(itemPosPlaybackNow==position) R.drawable.ic_pause_blue else R.drawable.ic_play_blue)
    }

    override fun getItemCount():Int = mValues.size

    fun remove(item: AudioModel) {
        stopPlay()
        val index=mValues.indexOf(item)
        if(index!=-1)
        {
            mValues.removeAt(index)
            notifyItemRemoved(index)
        }
    }
    fun getPlayAudioModelNow():AudioModel?
    {
        return if(itemPosPlaybackNow!=-1)
            mValues[itemPosPlaybackNow]
        else null
    }
    fun startPlay(audioModel: AudioModel)
    {
        stopPlay()
        itemPosPlaybackNow=mValues.indexOf(audioModel)
        if(itemPosPlaybackNow!=-1) notifyItemChanged(itemPosPlaybackNow)

    }
    fun stopPlay()
    {
        if(itemPosPlaybackNow!=-1)
        {
            val prevPos=itemPosPlaybackNow
            itemPosPlaybackNow=-1
            notifyItemChanged(prevPos)
        }
    }

    fun clear() {
        val size = mValues.size
        mValues.clear()
        notifyItemRangeRemoved(0, size)
    }
    fun setIdForUploadedPhoto(filename:String, newKey:Long)
    {
        mValues.firstOrNull { it.src==filename&&it.id==-1L }?.id=newKey
    }
    fun addData(item: AudioModel) {
        mValues.add(item)
        notifyItemInserted(mValues.size)
    }
    fun addAll(items: List<AudioModel>?) {
        items?.let {
            mValues.addAll(it)
            notifyItemRangeInserted(mValues.size-items.size, items.size)
        }
    }
}
