package com.clearlee.autosendwechatmsg;

import android.app.Application;

import com.lzy.okgo.OkGo;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        OkGo.getInstance().init(this);
        super.onCreate();
    }
}
