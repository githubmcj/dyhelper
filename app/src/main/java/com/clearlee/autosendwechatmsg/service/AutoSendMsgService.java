package com.clearlee.autosendwechatmsg.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadTask;
import com.clearlee.autosendwechatmsg.bean.Data;
import com.clearlee.autosendwechatmsg.common.WeChatTextWrapper;
import com.clearlee.autosendwechatmsg.net.BaseExt;
import com.clearlee.autosendwechatmsg.net.BaseResult;
import com.clearlee.autosendwechatmsg.net.BaseSubscriber;
import com.clearlee.autosendwechatmsg.net.api.ResultApi;
import com.wya.utils.utils.FileManagerUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.DST_FOLDER_NAME;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.PARENT_PATH;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.WechatId.DOUYINID_CHOSE_PHOTO_ID;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.WechatId.DOUYINID_CLICK_VIDEO_ID;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.WechatId.DOUYINID_CONTACTUI_DEAL_NEXT_ID;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.WechatId.DOUYINID_CONTACTUI_FIRST_NEXT_ID;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.WechatId.DOUYINID_CONTACTUI_NEXT_PUBLISH_ID;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.WechatId.DOUYINID_CONTACTUI_PUBLISH_ID;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.WechatId.DOUYINID_COPY_ID;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.WechatId.DOUYINID_LIKE_TEXTVIEW_ID;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.WechatId.DOUYINID_PUBLISH_EDITTEXT_ID;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.WechatId.DOUYINID_PUBLISH_ID;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.WechatId.DOUYINID_PUBLISH_SAVE_VIDEO_ID;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.WechatId.DOUYINID_RECOMMEND_ID;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.WechatId.DOUYINID_SHARE_ID;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.WechatId.DOUYINID_WARN_VIDEO_ID;
import static com.clearlee.autosendwechatmsg.util.WechatUtils.getClipboardContentTest;
import static com.clearlee.autosendwechatmsg.util.WechatUtils.performClick;
import static com.wya.utils.utils.FileManagerUtil.TASK_CANCEL;
import static com.wya.utils.utils.FileManagerUtil.TASK_COMPLETE;
import static com.wya.utils.utils.FileManagerUtil.TASK_FAIL;
import static com.wya.utils.utils.FileManagerUtil.TASK_RESUME;
import static com.wya.utils.utils.FileManagerUtil.TASK_RUNNING;
import static com.wya.utils.utils.FileManagerUtil.TASK_START;
import static com.wya.utils.utils.FileManagerUtil.TASK_STOP;

/**
 * Created by Clearlee
 * 2017/12/22.
 */
public class AutoSendMsgService extends AccessibilityService {

    private static final String TAG = "AutoSendMsgService";
    private static final String TAG_STEP = "step";
    private static final String TAG_SEARCH = "step_search";
    private String title = "";

    public static String step_str = "";
    private boolean first = false;
    public static int maxNum = -1;
    private int share_height = 0;

    private FileManagerUtil mFileManagerUtil = new FileManagerUtil();
    private List<DownloadEntity> mData = new ArrayList<>();

    private String storagePath = PARENT_PATH.getAbsolutePath() + File.separator + DST_FOLDER_NAME;


    /**
     * 必须重写的方法，响应各种事件。
     *
     * @param event
     */
    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: {
                String currentActivity = event.getClassName().toString();
                Log.e("---currentActivity---", currentActivity);
                if (currentActivity.equals(WeChatTextWrapper.DouyinClass.DOUYIN_CLASS_LAUNCHUI) && step_str.equals("")) {
                    // 刚进来
                    first = true;
                    toStartClickShare();
                } else if (currentActivity.equals(WeChatTextWrapper.DouyinClass.DOUYIN_CLASS_TAKE_PHOTO)) {
                    // 点击发布
                    toClickChoseVideo();
                } else if (currentActivity.equals(WeChatTextWrapper.DouyinClass.DOUYIN_CLASS_CHOSE_VIDEO)) {
                    // 选择并且下一步视频
                    toChoseVideo();
                } else if (currentActivity.equals(WeChatTextWrapper.DouyinClass.DOUYIN_CLASS_CUT_VIDEO)) {
                    // 视频剪切完后点击下一步
                    toCutVideoNext();
                } else if (currentActivity.equals(WeChatTextWrapper.DouyinClass.DOUYIN_CLASS_CUT_EDIT_VIDEO)) {
                    // 视频剪切完编辑点击下一步
                    toCutVideoEditNext();
                } else if (currentActivity.equals(WeChatTextWrapper.DouyinClass.DOUYIN_CLASS_PUBLISH)) {
                    // 发布页面
                    if (findViewByIdAndPasteContent(DOUYINID_PUBLISH_EDITTEXT_ID, title)) {
                        unSaveVideo();
                    }
                } else if (currentActivity.equals(WeChatTextWrapper.DouyinClass.DOUYIN_CLASS_PUBLISH_SUCCESS) && step_str.equals(DOUYINID_CONTACTUI_PUBLISH_ID)) {
                    // 点击了发布后，进入首页
                    deleteFile(new File(storagePath));
                    toStartClickRecommend();
                }
            }
            break;
            default:
                break;
        }
    }


    //flie：要删除的文件夹的所在位置
    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 点击推荐
     */
    private void toStartClickRecommend() {
        Log.e(TAG_STEP, "点击推荐");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AccessibilityNodeInfo accessibilityNodeInfo = this.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        step_str = DOUYINID_RECOMMEND_ID;
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(DOUYINID_RECOMMEND_ID);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performClick(nodeInfo);
                    // 执行完之后划出复制链接
                    break;
                }
            }
        }
        moveToNext();
    }

    /**
     * 点击喜欢
     */
    private boolean isLessLike() {
        Log.e(TAG_STEP, "检查点赞数");
        boolean isLessLike = true;
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AccessibilityNodeInfo accessibilityNodeInfo = this.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return isLessLike;
        }
        recycle(accessibilityNodeInfo);

        step_str = DOUYINID_LIKE_TEXTVIEW_ID;
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(DOUYINID_LIKE_TEXTVIEW_ID);

        Rect rect = new Rect();
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (int i = 0; i < nodeInfoList.size(); i++) {
                nodeInfoList.get(i).getBoundsInScreen(rect);
                if (rect.top == 890) {
                    if (nodeInfoList.get(i).getText().toString().contains("w")) {
                        double num = Double.valueOf(nodeInfoList.get(i).getText().toString().replace("w", ""));
                        if (num > maxNum) {
                            isLessLike = false;
                        } else {
                            isLessLike = true;
                            Log.e(TAG, "点赞数不足，不转发，直接下一个视频");
                        }
                    } else {
                        isLessLike = true;
                        Log.e(TAG, "点赞数不足，不转发，直接下一个视频");
                    }
                    break;
                }
            }
        }
        return isLessLike;
    }

    private boolean isWarnVideo() {
        boolean isWarn = false;
        Log.e(TAG_STEP, "警告检查");

        AccessibilityNodeInfo accessibilityNodeInfo = this.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            isWarn = true;
            return isWarn;
        }
        recycle(accessibilityNodeInfo);

        step_str = DOUYINID_WARN_VIDEO_ID;
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(DOUYINID_WARN_VIDEO_ID);
        Rect rect = new Rect();
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (int i = 0; i < nodeInfoList.size(); i++) {
                nodeInfoList.get(i).getBoundsInScreen(rect);
                if (rect.top == 1352) {
                    if (nodeInfoList.get(i).getText().length() > 0) {
                        isWarn = true;
                        Log.e(TAG, "警告视频，不转发");
                    } else {
                        isWarn = false;
                    }
                    break;
                }
            }
        }
        return isWarn;
    }


    /**
     * 分享，延迟一秒执行
     */
    private void toStartClickShare() {
        Log.e(TAG_STEP, "分享，延迟执行");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clickShare();
    }

    /**
     * 点击分享
     */
    private void clickShare() {
        if (isWarnVideo() || isLessLike()) {
            moveToNext();
            return;
        }
        Log.e(TAG_STEP, "点击分享");
        AccessibilityNodeInfo accessibilityNodeInfo = this.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        step_str = DOUYINID_SHARE_ID;
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(DOUYINID_SHARE_ID);
        Rect rect = new Rect();
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (int i = 0; i < nodeInfoList.size(); i++) {
                nodeInfoList.get(i).getBoundsInScreen(rect);
                if (rect.top == 1090) {
                    performClick(nodeInfoList.get(i));
                    break;
                }
            }
        }
        moveToRight();
    }



    public void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            if ("android.widget.TextView".equals(info.getClassName())) {
                Log.i(TAG, "ResourceName：" + info.getViewIdResourceName() + "---Text：" + info.getText() + "---windowId：" + info.getWindowId() + "---childwidget：" + info.getClassName());
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i));
                }
            }
        }
    }


    /**
     * 延迟毫秒执行手势滑动到下一个
     */
    private void moveToRight() {
        Log.e(TAG_STEP, "延迟毫秒执行手势滑动到下一个");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int xValue = displayMetrics.widthPixels;
            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            Path path = new Path();
            path.moveTo(xValue / 10 * 9, 1300);
            path.lineTo(0, 1300);
            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 50, 500));
            dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    clickCopy();
                    super.onCompleted(gestureDescription);

                }


            }, null);
        }
    }

    /**
     * 点击复制链接地址
     */
    private void clickCopy() {
        Log.e(TAG_STEP, "点击复制链接地址");
        AccessibilityNodeInfo accessibilityNodeInfo = this.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        step_str = DOUYINID_COPY_ID;
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(DOUYINID_COPY_ID);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && (nodeInfo.getText() != null && nodeInfo.getText().equals("复制链接"))) {
                    performClick(nodeInfo);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG_SEARCH, getClipboardContentTest(AutoSendMsgService.this));
                    getData(getUrl(getClipboardContentTest(AutoSendMsgService.this)));
                }
            }
        }
    }


    /**
     * 滑到下一个
     */
    private void moveToNext() {
        Log.e(TAG_STEP, "滑到下一个");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int middleYValue = displayMetrics.heightPixels / 2;
            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            Path path = new Path();

            path.moveTo(0, middleYValue);
            path.lineTo(0, 0);

            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 100, 50));
            dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    clickShare();
                    super.onCompleted(gestureDescription);
                }
            }, null);
        }
    }

    private ResultApi resultApi = new ResultApi();

    private void getData(final String url) {
        BaseExt.ext(resultApi.startApi(url), new BaseSubscriber<BaseResult<Data>>() {
            @Override
            public void onNext(BaseResult<Data> baseResult) {
                if (baseResult.status == 1) {
                    Log.e("【" + baseResult.data.getTitle() + "】无水印抖音视频地址：", baseResult.data.getVideourl());
                    toDownload(baseResult.data);
                } else {
                    moveToNext();
                    Toast.makeText(AutoSendMsgService.this, baseResult.msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("【" + url + "】无水印抖音视频地址：", "---------------------失败-------------");
                moveToNext();
            }

            @Override
            public void onComplete() {
                super.onComplete();
            }
        });
    }

    private void toDownload(Data data) {
        Log.e(TAG, data.getTitle() + "-----开始下载");

        if (mFileManagerUtil.getDownloadReceiver() == null) {
            mFileManagerUtil.register();
        }
        String filePath = storagePath + data.getTitle() + System.currentTimeMillis() + ".mp4";
        mFileManagerUtil.getDownloadReceiver().load(data.getVideourl())
                .setFilePath(filePath)
                .start();
        mFileManagerUtil.setOnDownLoaderListener(new FileManagerUtil.OnDownLoaderListener() {
            @Override
            public void onDownloadState(int state, DownloadTask task, Exception e) {
                for (int i = 0; i < mData.size(); i++) {
                    if (mData.get(i).getKey().equals(task.getKey())) {
                        switch (state) {
                            case TASK_START:
                                Log.e(TAG, "开始下载");
                                break;
                            case TASK_RUNNING:
                                mData.set(i, task.getEntity());
                                break;
                            case TASK_RESUME:
                                break;
                            case TASK_COMPLETE:
                                mData.remove(i);
                                Log.e(TAG, "下载完成");
                                toPublish(filePath);
                                break;
                            case TASK_FAIL:
                                break;
                            case TASK_STOP:
                                mData.set(i, task.getEntity());
                                break;
                            case TASK_CANCEL:
                                getNotcomplete();
                                break;
                            default:
                                break;
                        }
                    }
                }
                if (state == TASK_COMPLETE) {
                    toPublish(filePath);

                }
            }
        });
    }

    /**
     * 点击加号发布
     */
    private void toPublish(String filePath) {
        Log.e(TAG_STEP, "点击加号发布");
        //发送广播通知系统图库刷新数据
        Uri uri = Uri.fromFile(new File(filePath));
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

        AccessibilityNodeInfo accessibilityNodeInfo = this.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(DOUYINID_PUBLISH_ID);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performClick(nodeInfo);
                }
            }
        }
    }

    /**
     * 上传视频
     */
    private void toClickChoseVideo() {
        Log.e(TAG_STEP, "上传视频");
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        AccessibilityNodeInfo accessibilityNodeInfo = this.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        step_str = DOUYINID_CHOSE_PHOTO_ID;
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(DOUYINID_CHOSE_PHOTO_ID);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performClick(nodeInfo);
                }
            }
        }
    }


    /**
     * 选择视频
     */
    private void toChoseVideo() {
        Log.e(TAG_STEP, "选择视频");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        AccessibilityNodeInfo accessibilityNodeInfo = this.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        step_str = DOUYINID_CLICK_VIDEO_ID;
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(DOUYINID_CLICK_VIDEO_ID);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performClick(nodeInfo);
                    break;
                }
            }
        }

        toClickNext();

    }

    /**
     * 选择视频后下一步
     */
    private void toClickNext() {
        Log.e(TAG_STEP, "选择视频后下一步");
        AccessibilityNodeInfo accessibilityNodeInfo = this.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        step_str = DOUYINID_CONTACTUI_FIRST_NEXT_ID;
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(DOUYINID_CONTACTUI_FIRST_NEXT_ID);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performClick(nodeInfo);
                    break;
                }
            }
        }
    }

    /**
     * 剪切页面点击下一步
     */
    private void toCutVideoNext() {
        Log.e(TAG_STEP, "剪切页面点击下一步");
        AccessibilityNodeInfo accessibilityNodeInfo = this.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        step_str = DOUYINID_CONTACTUI_DEAL_NEXT_ID;
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(DOUYINID_CONTACTUI_DEAL_NEXT_ID);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performClick(nodeInfo);
                    break;
                }
            }
        }

    }

    /**
     * 编辑视频页面点击下一步
     */
    private void toCutVideoEditNext() {
        Log.e(TAG_STEP, "编辑视频页面点击下一步");
        AccessibilityNodeInfo accessibilityNodeInfo = this.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        step_str = DOUYINID_CONTACTUI_NEXT_PUBLISH_ID;
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(DOUYINID_CONTACTUI_NEXT_PUBLISH_ID);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performClick(nodeInfo);
                    break;
                }
            }
        }

    }


    private boolean findViewByIdAndPasteContent(String id, String content) {
        AccessibilityNodeInfo rootNode = this.getRootInActiveWindow();
        if (rootNode != null) {
            List<AccessibilityNodeInfo> editInfo = rootNode.findAccessibilityNodeInfosByViewId(id);
            if (editInfo != null && !editInfo.isEmpty()) {
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content);
                editInfo.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 不保存到本地
     */
    private void unSaveVideo() {
        AccessibilityNodeInfo rootNode = this.getRootInActiveWindow();
        if (rootNode != null) {
            List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId(DOUYINID_PUBLISH_SAVE_VIDEO_ID);
            if (null != list) {
                for (AccessibilityNodeInfo nodeInfo : list) {
                    if (nodeInfo.isChecked()) {
                        performClick(nodeInfo);
                    }
                }
            }
        }
        toPublishVideo();
    }


    /**
     * 发布视频
     */
    private void toPublishVideo() {
        Log.e(TAG_STEP, "发布视频");
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AccessibilityNodeInfo accessibilityNodeInfo = this.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        step_str = DOUYINID_CONTACTUI_PUBLISH_ID;
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(DOUYINID_CONTACTUI_PUBLISH_ID);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    first = false;
                    performClick(nodeInfo);
                    break;
                }
            }
        }

    }


    private void getNotcomplete() {
        List<DownloadEntity> allNotCompleteTask = mFileManagerUtil.getDownloadReceiver()
                .getAllCompleteTask();

        mData.clear();
        if (allNotCompleteTask != null) {
            mData.addAll(allNotCompleteTask);
        }
    }

    private String getUrl(String clipboardContentTest) {
        String url = "http" + clipboardContentTest.split("http")[1].split(" 复制此链接")[0];
        title = clipboardContentTest.split("http")[0];
        Log.e(TAG, "复制内容：" + url);
        Log.e(TAG, "标题：" + title);
        return url;
    }


    /**
     * 下载完回到APP
     */
    private void resetAndReturnApp() {
        ActivityManager activtyManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activtyManager.getRunningTasks(3);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos) {
            if (this.getPackageName().equals(runningTaskInfo.topActivity.getPackageName())) {
                activtyManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                return;
            }
        }
    }

    @Override
    public void onInterrupt() {
        mFileManagerUtil.unRegister();
        Log.e(TAG, "服务结束");
    }

}
