package com.example.bloold.buildp.adapter

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.bloold.buildp.api.data.CatalogObject
import com.example.bloold.buildp.single.`object`.DescriptionFragment
import com.example.bloold.buildp.single.`object`.photos.PhotoFragment
import com.example.bloold.buildp.ui.fragments.AudioListFragment
import com.example.bloold.buildp.ui.fragments.DocPubListFragment
import com.example.bloold.buildp.ui.fragments.VideoListFragment

/**
 * Created by bloold on 19.10.17.
 */
class CatalogObjectDetailsPagerAdapter(private val item: CatalogObject, fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

    companion object {
        private val DESCRIPTION = "Описание"
        private val PHOTO = "Фото"
        //private val STATE = "Состояние"
        private val MATERIALS = "Материалы"
        private val AUDIO = "Аудио"
        private val VIDEO = "Видео"
        private val PUBLICATIONS = "Публикации"

        private val KEY_DESCRIPTION = 0
        private val KEY_PHOTO = 1
        //private val KEY_STATE = 2
        private val KEY_MATERIALS = 2
        private val KEY_AUDIO = 3
        private val KEY_VIDEO = 4
        private val KEY_PUBLICATIONS = 5
    }

    enum class LIST_TYPE {
        SECURITY,
        MATERIALS,
        AUDIO,
        VIDEO,
        PUBLICATIONS
    }

    override fun getItem(position: Int): android.support.v4.app.Fragment? {

        when(position){
            KEY_DESCRIPTION -> {
                return DescriptionFragment.newInstance(item.previewText)
            } KEY_PHOTO -> {
                return PhotoFragment.newInstance(item.photosData)
            }
            KEY_MATERIALS -> {
                return DocPubListFragment.newInstance(item.docsData)
            }/*KEY_STATE -> {
                return
            }*/ KEY_AUDIO -> {
                return AudioListFragment.newInstance(item.audioData)
            } KEY_VIDEO -> {
                return VideoListFragment.newInstance(item.videoData)
            } KEY_PUBLICATIONS -> {
                return DocPubListFragment.newInstance(item.publicationsData)
            }
        }
        return null
    }

    override fun getCount(): Int {
        return 6
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            KEY_DESCRIPTION -> {
                return DESCRIPTION
            } KEY_PHOTO -> {
                return PHOTO
            } KEY_MATERIALS -> {
                return MATERIALS
            } KEY_AUDIO -> {
                return AUDIO
            } KEY_VIDEO -> {
                return VIDEO
            } KEY_PUBLICATIONS -> {
                return PUBLICATIONS
            }
        }
        return null
    }
}