package com.huya.marksman.util.rxretrofit;

import org.reactivestreams.Publisher;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by charles on 2018/5/15.
 */

public class ThreadTransformer<T> implements ObservableTransformer<T, T>
                                                ,FlowableTransformer<T, T>
                                                , SingleTransformer<T, T>
                                                , MaybeTransformer<T, T>
                                                , CompletableTransformer {
    private Scheduler mSubscribeOn;
    private Scheduler mObserverOn;

    public ThreadTransformer() {
    }

    public ThreadTransformer(Scheduler subscribeOn, Scheduler observerOn) {
        this.mSubscribeOn = subscribeOn;
        this.mObserverOn = observerOn;
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.subscribeOn(mSubscribeOn!=null?mSubscribeOn: Schedulers.io()).observeOn(mObserverOn!=null?mObserverOn: AndroidSchedulers.mainThread());
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        return upstream.subscribeOn(mSubscribeOn!=null?mSubscribeOn: Schedulers.io()).observeOn(mObserverOn!=null?mObserverOn:AndroidSchedulers.mainThread());
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream.subscribeOn(mSubscribeOn!=null?mSubscribeOn: Schedulers.io()).observeOn(mObserverOn!=null?mObserverOn:AndroidSchedulers.mainThread());
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        return upstream.subscribeOn(mSubscribeOn!=null?mSubscribeOn: Schedulers.io()).observeOn(mObserverOn!=null?mObserverOn:AndroidSchedulers.mainThread());
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream.subscribeOn(mSubscribeOn!=null?mSubscribeOn: Schedulers.io()).observeOn(mObserverOn!=null?mObserverOn:AndroidSchedulers.mainThread());
    }
}
