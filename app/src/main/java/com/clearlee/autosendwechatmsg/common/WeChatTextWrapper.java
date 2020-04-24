package com.clearlee.autosendwechatmsg.common;

import android.os.Environment;

import java.io.File;

/**
 * Created by Clearlee on 2017/12/22 0023.
 * 微信版本6.6.0
 */

public class WeChatTextWrapper {

    public static final String DOUYIN_PACKAGENAME = "com.ss.android.ugc.aweme";

    public static final File PARENT_PATH = Environment.getExternalStorageDirectory();
    public static String DST_FOLDER_NAME = "wya/dy_video";

    public static class DouyinClass{
        //抖音首页
        public static final String DOUYIN_CLASS_LAUNCHUI = "com.ss.android.ugc.aweme.main.MainActivity";
        // 抖音分享
        public static final String DOUYIN_CLASS_SHARE = "com.ss.android.ugc.aweme.share.improve.c";
        //微信联系人页面
        public static final String DOUYIN_CLASS_CONTACTINFOUI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";
        //微信聊天页面
        public static final String DOUYIN_CLASS_CHATUI = "com.tencent.mm.ui.chatting.ChattingUI";
    }


    public static class WechatId{
        /**
         * 分享按钮
         */
        public static final String DOUYINID_SHARE_ID = "com.ss.android.ugc.aweme:id/ejm";

        /**
         * 播放页面
         */
        public static final String DOUYINID_VIEWPAGER_ID = "com.ss.android.ugc.aweme:id/ebq";

        /**
         * 复制链接
         */
        public static final String DOUYINID_COPY_ID = "com.ss.android.ugc.aweme:id/eiz";
        public static final String DOUYINID_CONTACTUI_LISTVIEW_ID = "com.ss.android.ugc.aweme:id/ejm";
        public static final String DOUYINID_CONTACTUI_ITEM_ID = "com.tencent.mm:id/iy";
        public static final String DOUYINID_CONTACTUI_NAME_ID = "com.tencent.mm:id/j1";

        /**
         * 分享
         */
        public static final String DOUYINID_CHATUI_EDITTEXT_ID = "com.tencent.mm:id/a_z";
        public static final String DOUYINID_CHATUI_USERNAME_ID = "com.tencent.mm:id/ha";
        public static final String DOUYINID_CHATUI_BACK_ID = "com.tencent.mm:id/h9";
        public static final String DOUYINID_CHATUI_SWITCH_ID = "com.tencent.mm:id/a_x";
    }

}
