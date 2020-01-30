package com.zxwl.network.func;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * author：pc-20171125
 * data:2019/6/14 16:29
 */
public class CustomCompose implements Observable.Transformer {
    @Override
    public Object call(Object o) {
        return ((Observable) o)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(3, 300));
    }
}
