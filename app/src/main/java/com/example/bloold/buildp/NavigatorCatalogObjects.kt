package com.example.bloold.buildp

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import java.util.*

/**
 * Created by bloold on 17.10.17.
 */
class NavigatorCatalogObjects(private val activity: AppCompatActivity,
                              private val layoutContainerId: Int) {

    private val fragmentManager: FragmentManager = activity.supportFragmentManager
    var currentScreen: Stack<CatalogObjectsScreens> = Stack()
    var currentFragment: Fragment? = null

    enum class CatalogObjectsScreens {
        CATALOG_OBJECTS,
        FILTER_OBJECTS,
        SUB_FILTER_OBJECTS,
        CHECK_FILTER_OBJECTS
    }


    private fun showFragment(fragment: Fragment, screen: CatalogObjectsScreens) {
        if (currentScreen.peek() != screen) {

            currentFragment = fragment

            fragmentManager.beginTransaction()
                    .replace(layoutContainerId, fragment)
                    .commit()

            currentScreen.push(screen)
        }
    }

    fun initFragment(){
        //currentFragment = CatalogObjectListFragment.newInstance()

        fragmentManager.beginTransaction()
                .replace(layoutContainerId, currentFragment)
                .commit()

        //currentScreen.push(CatalogObjectsScreens.ALL_CATALOG_OBJECTS)
    }

    fun navigateTo(catalogObjects: CatalogObjectsScreens, data: Any? = null){
        when(catalogObjects){
            CatalogObjectsScreens.CATALOG_OBJECTS -> {
                //showFragment(CatalogObjectListFragment.newInstance(), catalogObjects)
            }
        }
    }
}