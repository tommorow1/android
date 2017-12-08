package com.example.bloold.buildp.components;

import android.support.v7.app.AppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Leonov Oleg, http://pandorika-it.com on 13.01.17.
 */

public class NetworkActivity extends AppCompatActivity
{
    private CompositeDisposable compositeDisposable;
    public CompositeDisposable getCompositeDisposable() {
        if(compositeDisposable==null)
            compositeDisposable = new CompositeDisposable();
        return compositeDisposable;
    }

    @Override
    public void onStop() {
        if(compositeDisposable!=null) {
            compositeDisposable.clear();
            compositeDisposable = null;
        }
        super.onStop();
    }
}
