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
    public static String DST_FOLDER_NAME = "wya/dy_video/";

    public static class DouyinClass {
        //抖音首页
        public static final String DOUYIN_CLASS_LAUNCHUI = "com.ss.android.ugc.aweme.main.MainActivity";
        // 抖音分享
        public static final String DOUYIN_CLASS_SHARE = "com.ss.android.ugc.aweme.share.improve.c";
        // 抖音发布视频
        public static final String DOUYIN_CLASS_TAKE_PHOTO = "com.ss.android.ugc.aweme.shortvideo.ui.VideoRecordNewActivity";
        // 选择视频
        public static final String DOUYIN_CLASS_CHOSE_VIDEO = "com.ss.android.ugc.aweme.shortvideo.mvtemplate.choosemedia.MvChoosePhotoActivity";
        // 视频剪切
        public static final String DOUYIN_CLASS_CUT_VIDEO = "com.ss.android.ugc.aweme.shortvideo.cut.VECutVideoActivity";
        // 视频剪切完后编辑
        public static final String DOUYIN_CLASS_CUT_EDIT_VIDEO = "com.ss.android.ugc.aweme.shortvideo.edit.VEVideoPublishEditActivity";
        // 发布页面
        public static final String DOUYIN_CLASS_PUBLISH = "com.ss.android.ugc.aweme.shortvideo.ui.VideoPublishActivity";
         // 发布成功
        public static final String DOUYIN_CLASS_PUBLISH_SUCCESS = "android.widget.FrameLayout";

    }


    public static class WechatId {
        /**
         * 推荐按钮
         */
        public static final String DOUYINID_RECOMMEND_ID = "com.ss.android.ugc.aweme:id/fu9";


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

        /**
         * 加号
         */
        public static final String DOUYINID_PUBLISH_ID = "com.ss.android.ugc.aweme:id/f04";

        /**
         * 点击上传视频
         */
        public static final String DOUYINID_CHOSE_PHOTO_ID = "com.ss.android.ugc.aweme:id/g5u";

         /**
         * 选中按钮
         */
        public static final String DOUYINID_CLICK_VIDEO_ID = "com.ss.android.ugc.aweme:id/bma";

        /**
         * 选中视频点击下一步
         */
        public static final String DOUYINID_CONTACTUI_FIRST_NEXT_ID = "com.ss.android.ugc.aweme:id/exm";

        /**
         * 处理视频后点击下一步
         */
        public static final String DOUYINID_CONTACTUI_DEAL_NEXT_ID = "com.ss.android.ugc.aweme:id/flm";

         /**
         * 点击下一步进入发布页面
         */
        public static final String DOUYINID_CONTACTUI_NEXT_PUBLISH_ID = "com.ss.android.ugc.aweme:id/d6v";

        /**
         * 点击发布
         */
        public static final String DOUYINID_CONTACTUI_PUBLISH_ID = "com.ss.android.ugc.aweme:id/dua";

        /**
         * 点赞控件id
         */
        public static final String DOUYINID_LIKE_TEXTVIEW_ID = "com.ss.android.ugc.aweme:id/alc";

        /**
         * 发布文本编辑框id
         */
        public static final String DOUYINID_PUBLISH_EDITTEXT_ID = "com.ss.android.ugc.aweme:id/asu";

        /**
         * 是否保存本地视频
         */
        public static final String DOUYINID_PUBLISH_SAVE_VIDEO_ID = "com.ss.android.ugc.aweme:id/a2s";

         /**
         * 警告提醒
         */
        public static final String DOUYINID_WARN_VIDEO_ID = "com.ss.android.ugc.aweme:id/g1d";


    }

}
