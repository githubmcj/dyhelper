package com.clearlee.autosendwechatmsg.net.api;


import com.clearlee.autosendwechatmsg.bean.Data;
import com.clearlee.autosendwechatmsg.net.BaseResult;
import com.clearlee.autosendwechatmsg.net.RetrofitFactory;

import java.util.HashMap;

import io.reactivex.Observable;

/**
 * @date: 2018/7/3 13:58
 * @author: Chunjiang Mao
 * @classname: ResultApi
 * @describe: 传参api
 */

public class ResultApi {

    /**
     * 获取数据
     *
     * @return
     */
    public Observable<BaseResult<Data>> startApi(String url) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("url", url);
        return RetrofitFactory.getInstance().create(Api.class).start(hashMap);
    }

}
