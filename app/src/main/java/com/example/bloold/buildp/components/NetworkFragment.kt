package com.example.bloold.buildp.components

import android.support.v4.app.Fragment

import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Leonov Oleg, http://pandorika-it.com on 13.01.17.
 */

open class NetworkFragment : Fragment() {
    /*** Используется свой CompositeDisposable, так как у фрагмента может быть свой жизненный цикл
     * иначе бы запрос висел даже при закрытии фрагмента и до закрытия activity
     */

    private var compositeDisposable: CompositeDisposable? = null

    protected fun clearDisposable() {
        compositeDisposable?.let {
            it.clear()
            compositeDisposable = null
        }
    }

    fun getCompositeDisposable(): CompositeDisposable {
        if (compositeDisposable == null)
            compositeDisposable = CompositeDisposable()
        return compositeDisposable as CompositeDisposable
    }

    override fun onStop() {
        clearDisposable()
        super.onStop()
    }
}
