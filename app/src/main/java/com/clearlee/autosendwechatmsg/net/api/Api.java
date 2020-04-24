package com.clearlee.autosendwechatmsg.net.api;

import com.clearlee.autosendwechatmsg.bean.Data;
import com.clearlee.autosendwechatmsg.net.BaseResult;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * @date: 2018/7/3 13:44
 * @author: Chunjiang Mao
 * @classname: Api
 * @describe: 请求数据的接口
 */

public interface Api {
    /**
     * 获取
     *
     * @param hashMap
     * @return
     */
    @GET("parseByLink.php")
    Observable<BaseResult<Data>> start(@QueryMap HashMap<String, String> hashMap);

}
