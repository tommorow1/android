package com.example.bloold.buildp.single.`object`.photos

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bloold.buildp.R
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.model.PhotoModel
import com.example.bloold.buildp.ui.ImageViewActivity

class PhotoFragment : Fragment()
{
    private var mColumnCount = 3
    private var photoList: ArrayList<PhotoModel>? = null
    private lateinit var adapter: PhotoRecyclerViewAdapter
    private lateinit var rvPhotos: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mColumnCount = 3

        arguments?.let { photoList= it.getParcelableArrayList<PhotoModel>(IntentHelper.EXTRA_PHOTO_DATA_ARRAY) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_photo_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (view is RecyclerView) {
            view.layoutManager = GridLayoutManager(view.context, mColumnCount)

            rvPhotos = view

            adapter = PhotoRecyclerViewAdapter(OnItemClickListener
            {
                currentPhoto->
                photoList?.let{ ImageViewActivity.launch(activity!!, it, currentPhoto) }
                /*startActivity(Intent(activity, ImageViewActivity::class.java)
                        .putExtra(IntentHelper.EXTRA_IMAGE_URL, it.fullPath()))*/
            }, view.context)
            adapter.addAll(photoList?.toList())

            rvPhotos.adapter = adapter
        }
    }

    companion object {
        fun newInstance(photos: ArrayList<PhotoModel>?): PhotoFragment
                = PhotoFragment().apply {  arguments = Bundle().apply { putParcelableArrayList(IntentHelper.EXTRA_PHOTO_DATA_ARRAY, photos) } }
    }
}
