package com.clearlee.autosendwechatmsg;

import android.app.Application;

import com.arialyy.aria.core.Aria;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        Aria.init(this);
        super.onCreate();
    }
}
