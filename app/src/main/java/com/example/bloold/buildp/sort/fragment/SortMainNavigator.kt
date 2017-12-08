package com.example.bloold.buildp.sort.fragment

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.example.bloold.buildp.R
import com.example.bloold.buildp.model.Category
import com.example.bloold.buildp.ui.fragments.CatalogObjectListFragment
import com.example.bloold.buildp.model.HightFilterModelLevel
import com.example.bloold.buildp.model.SortObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by bloold on 22.10.17.
 */
interface onFilterListener {
    fun onScreenNavigate(screen: FilterMainNavigator.FilterScreens)
}

class FilterMainNavigator(private val activity: AppCompatActivity,
                          private val layoutContainerId: Int,
                          private val listener: onFilterListener) {

    private lateinit var items: ArrayList<HightFilterModelLevel>

    private val fragmentManager: FragmentManager = activity.supportFragmentManager
    var currentScreen: Stack<FilterScreens> = Stack()
    var currentFragment: Stack<Fragment> = Stack()

    enum class FilterScreens {
        //MAIN_FILTER,
        CATALOG_OBJECTS,
        //FILTER
    }


    private fun showFragment(fragment: Fragment, screen: FilterScreens) {

        if(currentScreen.empty()){
            currentFragment.push(fragment)

            fragmentManager.beginTransaction()
                    .replace(layoutContainerId, fragment)
                    .commit()

            currentScreen.push(screen)

        } else {
            currentFragment.push(fragment)

            fragmentManager.beginTransaction()
                    .replace(layoutContainerId, fragment)
                    .addToBackStack(screen.name)
                    .commit()

            currentScreen.add(screen)
        }

        listener.onScreenNavigate(screen)
    }

    fun navigateTo(filterScreens: FilterScreens, data: Any? = null, category: Category? = null){
        when(filterScreens){
            /*FilterScreens.MAIN_FILTER -> {
                if(data != null) {
                    showFragment(SortFragment.newInstance(data as ArrayList<Category>, R.layout.item_hight_level_filter), filterScreens)
                }
            }*/
            FilterScreens.CATALOG_OBJECTS -> showFragment(CatalogObjectListFragment.newInstance(category), filterScreens)
/*            FilterScreens.FILTER -> {
            showFragment(ChooseCatalogFiltersFragment.newInstance((currentFragment.peek() as CatalogObjectListFragment).sortedObject), FilterScreens.FILTER)
        }*/
        }
    }

    fun back(){
        if(currentScreen.size > 1) {
            currentScreen.pop()
            currentFragment.pop()
            listener.onScreenNavigate(currentScreen.peek())
        }
    }
}