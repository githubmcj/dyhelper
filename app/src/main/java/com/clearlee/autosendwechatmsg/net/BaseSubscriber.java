package com.clearlee.autosendwechatmsg.net;

import android.util.Log;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @date: 2018/5/31 13:59
 * @author: Chunjiang Mao
 * @classname: BaseSubscriber
 * @describe: 写一个Subscriber继承Observer，重写相应的方法，在具体调用时，只需要重写你需要的onNext方法即可，不需要写入其余的回调方法
 */

public class BaseSubscriber<T> implements Observer<T> {
    private Disposable disposable;

    public BaseSubscriber() {

    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public void onNext(T t) {
    }

    @Override
    public void onError(Throwable e) {
        Log.e("error:", e.toString());
    }

    @Override
    public void onComplete() {
    }
}
