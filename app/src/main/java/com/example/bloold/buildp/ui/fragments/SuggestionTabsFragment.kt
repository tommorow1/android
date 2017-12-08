package com.example.bloold.buildp.ui.fragments

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bloold.buildp.R
import com.example.bloold.buildp.api.ApiHelper
import com.example.bloold.buildp.components.NetworkFragment
import com.example.bloold.buildp.databinding.FragmentSuggestionTabsBinding
import kotlinx.android.synthetic.main.app_bar_main.*
import java.lang.ref.WeakReference

class SuggestionTabsFragment : NetworkFragment() {
    private lateinit var mBinding: FragmentSuggestionTabsBinding
    private lateinit var suggestionPagerAdapter: SuggestionPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_suggestion_tabs, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        suggestionPagerAdapter = SuggestionPagerAdapter(childFragmentManager)
        mBinding.viewPager.adapter = suggestionPagerAdapter
        mBinding.tabs.setupWithViewPager(mBinding.viewPager)
    }

    override fun onResume() {
        super.onResume()
        activity?.toolbar?.setTitle(R.string.my_suggestions)
    }

    private inner class SuggestionPagerAdapter internal constructor(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        val allFragments = SparseArray<WeakReference<SuggestionsFragment>>()

        override fun getItem(i: Int): Fragment? {
            return when (i) {
                0 -> SuggestionsFragment.newInstance(ApiHelper.SUGGESTION_ALL)
                1 -> SuggestionsFragment.newInstance(ApiHelper.SUGGESTION_ON_MODERATION)
                2 -> SuggestionsFragment.newInstance(ApiHelper.SUGGESTION_APPROVED)
                3 -> SuggestionsFragment.newInstance(ApiHelper.SUGGESTION_DECLINED)
                else -> null
            }
        }

        override fun getCount(): Int {
            return 4
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(R.string.all)
                1 -> getString(R.string.suggestion_on_expert_checking)
                2 -> getString(R.string.suggestion_approved)
                3 -> getString(R.string.suggestion_declined)
                else -> null
            }
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val newObject = super.instantiateItem(container, position)
            allFragments.append(position, WeakReference(newObject as SuggestionsFragment))
            return newObject
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            allFragments.delete(position)
            super.destroyItem(container, position, `object`)
        }
    }

    companion object {

        fun newInstance(): SuggestionTabsFragment {
            return SuggestionTabsFragment()
        }
    }
}