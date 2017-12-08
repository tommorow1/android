package com.example.bloold.buildp.common;

import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Leonov Oleg, http://pandorika-it.com on 16.03.17.
 */

public class RxHelper {
    public static <T> SingleTransformer<T, T> applySchedulers() {
        return upstream -> upstream.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
