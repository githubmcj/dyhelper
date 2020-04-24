package com.clearlee.autosendwechatmsg.net;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @date: 2018/5/31 13:58
 * @author: Chunjiang Mao
 * @classname: BaseExt
 * @describe: 基类Observanle，防止多次写相同代码
 */

public class BaseExt {
    public static <T> void ext(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
