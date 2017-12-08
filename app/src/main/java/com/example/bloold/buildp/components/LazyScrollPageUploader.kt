package com.example.bloold.buildp.components

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Created by Leonov Oleg, http://pandorika-it.com on 23.10.16.
 */

class LazyScrollPageUploader(private val onLazyScrollUploaderListener: OnLazyScrollUploaderListener) : RecyclerView.OnScrollListener() {
    var noMoreElements = false
    private var loading = false

    fun setLoading(loading: Boolean) {
        this.loading = loading
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (loading || noMoreElements) return

        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        val totalItemCount = linearLayoutManager.itemCount
        val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
        //int firstVisible=linearLayoutManager.findFirstVisibleItemPosition();
        if (totalItemCount <= lastVisibleItem + ITEMS_IN_LIST_LEFT_BEFORE_REQUEST) {
            loading = true
            onLazyScrollUploaderListener.onLoadData(totalItemCount)
        }
    }

    interface OnLazyScrollUploaderListener {
        fun onLoadData(offset: Int)
    }

    companion object {
        private val ITEMS_IN_LIST_LEFT_BEFORE_REQUEST = 2
    }
}