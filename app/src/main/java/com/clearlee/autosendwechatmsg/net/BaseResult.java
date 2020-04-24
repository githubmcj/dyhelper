package com.clearlee.autosendwechatmsg.net;

/**
 * @date: 2018/7/3 13:50
 * @author: Chunjiang Mao
 * @classname: BaseResult
 * @describe:
 */

public class BaseResult<T> {
    public int status;
    public String msg;
    public T data;
}
