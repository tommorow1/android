package com.example.bloold.buildp.components

import android.content.Context
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

/**
 * Created by Leonov Oleg, http://pandorika-it.com on 24.08.17.
 */

class SpinnerWithoutLPaddingAdapter<T> : ArrayAdapter<T> {

    constructor(context: Context, @LayoutRes resource: Int) : super(context, resource) {}

    constructor(context: Context, @LayoutRes resource: Int, @IdRes textViewResourceId: Int) : super(context, resource, textViewResourceId) {}

    constructor(context: Context, @LayoutRes resource: Int, objects: Array<T>) : super(context, resource, objects) {}

    constructor(context: Context, @LayoutRes resource: Int, @IdRes textViewResourceId: Int, objects: Array<T>) : super(context, resource, textViewResourceId, objects) {}

    constructor(context: Context, @LayoutRes resource: Int, objects: List<T>) : super(context, resource, objects) {}

    constructor(context: Context, @LayoutRes resource: Int, @IdRes textViewResourceId: Int, objects: List<T>) : super(context, resource, textViewResourceId, objects) {}

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        view.setPadding(0, view.paddingTop, view.paddingRight, view.paddingBottom)
        return view
    }
}
