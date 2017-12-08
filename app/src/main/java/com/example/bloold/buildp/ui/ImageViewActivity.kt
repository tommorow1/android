package com.example.bloold.buildp.ui

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.view.PagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.bloold.buildp.R
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.databinding.ActivityImageBinding
import com.example.bloold.buildp.model.PhotoModel
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.synthetic.main.activity_image.*


/**
 * Created by Leonov Oleg, http://pandorika-it.com on 25.05.16.
 */
class ImageViewActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityImageBinding

    companion object {
        private val START_IMAGE_POS="startImagePos"
        fun launch(activity: FragmentActivity, photos: ArrayList<PhotoModel>, currentPhoto:PhotoModel?=null)
        {
            var currentImgPos=0
            currentPhoto?.let {
                currentImgPos=photos.indexOf(currentPhoto)
                if(currentImgPos==-1) currentImgPos=0
            }
            activity.startActivity(Intent(activity, ImageViewActivity::class.java)
                    .putExtra(IntentHelper.EXTRA_PHOTO_DATA_ARRAY, photos)
                    .putExtra(START_IMAGE_POS, currentImgPos)
            )
        }
        fun launch(activity: FragmentActivity, imageUrl:String)
        {
            activity.startActivity(Intent(activity, ImageViewActivity::class.java)
                    .putExtra(IntentHelper.EXTRA_IMAGE_URL, imageUrl))
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var imageList:List<String>?=null
        intent.getStringExtra(IntentHelper.EXTRA_IMAGE_URL)?.let { imageList= listOf(it) }
        if(imageList==null)
            intent.getParcelableArrayListExtra<PhotoModel>(IntentHelper.EXTRA_PHOTO_DATA_ARRAY)?.let { imageList= it.map { (it as PhotoModel).fullPath() } }
        imageList?.let {
            viewPager.adapter=ImagePagerAdapter(it)
            viewPager.currentItem=intent.getIntExtra(START_IMAGE_POS, 0)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private class ImagePagerAdapter(val images:List<String>): PagerAdapter()
    {
        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj as PhotoView
        }

        override fun getCount()
                = images.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val photoView=PhotoView(container.context)
            container.addView(photoView)
            Glide.with(container.context)
                    .load(images[position])
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(photoView)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as PhotoView)
        }
    }
}
