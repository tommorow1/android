package com.example.bloold.buildp.catalog.`object`

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.bloold.buildp.ui.fragments.CatalogObjectListFragment

/**
 * Created by bloold on 18.10.17.
 */
class PagerClassAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

    companion object {
        private val ALL_OBJECT = "http://ruinnet.idefa.ru/api_app/object/list/?select[]=ID&select[]=NAME&select[]=PREVIEW_TEXT&select[]=PROPERTY_ADDRESS&select[]=DETAIL_PICTURE&select[]=PHOTOS_DATA&select[]=DOCS_DATA&select[]=PUBLICATIONS_DATA&select[]=VIDEO_DATA&select[]=AUDIO_DATA&select[]=DETAIL_PAGE_URL&select[]=IS_FAVORITE&select[]=PROPERTY_MAP=Y&filter[INCLUDE_SUBSECTIONS]=Y&filter[IBLOCK_SECTION_ID][1]=7"
        private val SINGLE_OBJECT = "http://ruinnet.idefa.ru/api_app/object/list/?select[]=ID&select[]=NAME&select[]=PREVIEW_TEXT&select[]=PROPERTY_ADDRESS&select[]=DETAIL_PICTURE&select[]=PHOTOS_DATA&select[]=DOCS_DATA&select[]=PUBLICATIONS_DATA&select[]=VIDEO_DATA&select[]=AUDIO_DATA&select[]=DETAIL_PAGE_URL&select[]=IS_FAVORITE&filter[IBLOCK_SECTION_ID][1]=18&filter[INCLUDE_SUBSECTIONS]=Y&limit=20"
        private val GROUP_OBJECT = "http://ruinnet.idefa.ru/api_app/object/list/?select[]=ID&select[]=NAME&select[]=PREVIEW_TEXT&select[]=PROPERTY_ADDRESS&select[]=DETAIL_PICTURE&select[]=PHOTOS_DATA&select[]=DOCS_DATA&select[]=PUBLICATIONS_DATA&select[]=VIDEO_DATA&select[]=AUDIO_DATA&select[]=DETAIL_PAGE_URL&select[]=IS_FAVORITE&select[]=PROPERTY_MAP=Y&filter[INCLUDE_SUBSECTIONS]=Y&filter[INCLUDE_SUBSECTIONS]=Y&filter[IBLOCK_SECTION_ID][1]=28&filter[INCLUDE_SUBSECTIONS]=Y&limit=20"
        private val ATTRACTION_OBJECT = "http://ruinnet.idefa.ru/api_app/object/list/?select[]=ID&select[]=NAME&select[]=PREVIEW_TEXT&select[]=PROPERTY_ADDRESS&select[]=DETAIL_PICTURE&select[]=PHOTOS_DATA&select[]=DOCS_DATA&select[]=PUBLICATIONS_DATA&select[]=VIDEO_DATA&select[]=AUDIO_DATA&select[]=DETAIL_PAGE_URL&select[]=IS_FAVORITE&select[]=PROPERTY_MAP=Y&filter[INCLUDE_SUBSECTIONS]=Y&filter[INCLUDE_SUBSECTIONS]=Y&filter[IBLOCK_SECTION_ID][1]=39&filter[INCLUDE_SUBSECTIONS]=Y&limit=20"

        private val RELIGIOUS_OBJECT = "http://ruinnet.idefa.ru/api_app/object/list/?select[0]=NAME&filter[IBLOCK_SECTION_ID]=22&filter[INCLUDE_SUBSECTIONS]=Y&limit=20&select[1]=IBLOCK_SECTION_ID"
        private val RELIGIOUS_FEDERAL_OBJECT = "http://ruinnet.idefa.ru/api_app/object/list/?select[0]=NAME&filter[IBLOCK_SECTION_ID]=22&filter[INCLUDE_SUBSECTIONS]=Y&limit=20&filter[PROPERTY_VALUE_CATEGORY]=30"
        private val RELIGIOUS_FEDERAL_OBJECT_MONUMENT = "http://ruinnet.idefa.ru/api_app/object/list/?select[0]=NAME&filter[IBLOCK_SECTION_ID]=22&filter[INCLUDE_SUBSECTIONS]=Y&limit=20&filter[PROPERTY_VALUE_CATEGORY]=30&filter[PROPERTY_TYPE]=35"

        private val ALL_OBJECT_POSITION = 0
        private val SINGLE_OBJECT_POSITION = 1
        private val GROUP_OBJECT_POSITION = 2
        private val ATTRACTION_OBJECT_POSITION = 3

    }

    override fun getItem(position: Int): android.support.v4.app.Fragment {
        var urlResponse: String = ""

        when(position){
            ALL_OBJECT_POSITION -> {
                urlResponse = ALL_OBJECT
            } SINGLE_OBJECT_POSITION -> {
                urlResponse = SINGLE_OBJECT
            } GROUP_OBJECT_POSITION -> {
                urlResponse = GROUP_OBJECT
            } ATTRACTION_OBJECT_POSITION -> {
                urlResponse = ATTRACTION_OBJECT
            }
        }

        //return CatalogObjectListFragment.newInstance(urlResponse)
        //TODO сделать дял разных табов свой список
        return CatalogObjectListFragment.newInstance(null)
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "Все"
            1 -> return "Одиночные объекты"
            2 -> return "Группы объектов, комплексы и ансамбли"
            3 -> return "Достопримечательные места"
        }
        return null
    }
}