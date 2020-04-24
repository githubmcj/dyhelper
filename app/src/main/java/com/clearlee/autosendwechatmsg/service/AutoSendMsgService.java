package com.clearlee.autosendwechatmsg.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Path;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.clearlee.autosendwechatmsg.bean.Data;
import com.clearlee.autosendwechatmsg.common.WeChatTextWrapper;
import com.clearlee.autosendwechatmsg.net.BaseExt;
import com.clearlee.autosendwechatmsg.net.BaseResult;
import com.clearlee.autosendwechatmsg.net.BaseSubscriber;
import com.clearlee.autosendwechatmsg.net.api.ResultApi;
import com.clearlee.autosendwechatmsg.util.WechatUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.DST_FOLDER_NAME;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.PARENT_PATH;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.WechatId.DOUYINID_COPY_ID;
import static com.clearlee.autosendwechatmsg.common.WeChatTextWrapper.WechatId.DOUYINID_SHARE_ID;
import static com.clearlee.autosendwechatmsg.util.WechatUtils.getClipboardContentTest;
import static com.clearlee.autosendwechatmsg.util.WechatUtils.performClick;

/**
 * Created by Clearlee
 * 2017/12/22.
 */
public class AutoSendMsgService extends AccessibilityService {

    private static final String TAG = "AutoSendMsgService";
    private List<String> allNameList = new ArrayList<>();
    private int mRepeatCount;

    public static boolean hasSend;
    public static final int SEND_FAIL = 0;
    public static final int SEND_SUCCESS = 1;
    public static int SEND_STATUS;

    public static int step = 0;
    public static int maxNum = -1;
    private OkHttpClient.Builder builder = new OkHttpClient.Builder();

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
                if (currentActivity.equals(WeChatTextWrapper.DouyinClass.DOUYIN_CLASS_LAUNCHUI) && step == 0) {
                    toStartClickShare();
                }
            }
            break;
            default:
                break;
        }
    }

    /**
     * 分享，延迟一秒执行
     */
    private void toStartClickShare() {
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
        AccessibilityNodeInfo accessibilityNodeInfo = this.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        recycle(accessibilityNodeInfo);
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(DOUYINID_SHARE_ID);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performClick(nodeInfo);
                    step = 1;
                    // 执行完之后划出复制链接
                    moveToRight();
                }
            }
        }
    }

    public void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            Log.i(TAG, "ResourceName：" + info.getViewIdResourceName() + "---Text：" + info.getText() + "---windowId：" + info.getWindowId() + "---childwidget：" + info.getClassName());
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
        AccessibilityNodeInfo accessibilityNodeInfo = this.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
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
                    getData(getUrl(getClipboardContentTest(AutoSendMsgService.this)));

                }
            }
        }
    }


    /**
     * 滑动完后点击分享
     */
    private void toClickShare() {
        WechatUtils.findViewIdAndClick(this, DOUYINID_SHARE_ID);
        step = 1;
        // 执行完之后划出复制链接
        moveToRight();
    }

    private void moveToNext() {
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
                    toClickShare();
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
                moveToNext();
                super.onComplete();
            }
        });
    }

    private void toDownload(Data data) {
        OkGo.<File>get(data.getVideourl())
                .tag(data.getUrl())
                .execute(new FileCallback(storagePath, data.getTitle()+System.currentTimeMillis() + ".mp4") {

                    @Override
                    public void onStart(Request<File, ? extends Request> request) {
                        Log.e("OkGo", "正在下载中");
                    }

                    @Override
                    public void onSuccess(Response<File> response) {
                        response.body().getAbsolutePath();
                        Log.e("OkGo", "下载完成");
                    }

                    @Override
                    public void onError(Response<File> response) {
                        handleError(response);
                        Log.e("OkGo", "下载完成");
                    }

                    @Override
                    public void downloadProgress(Progress progress) {
//                        System.out.println(progress);
                        String downloadLength = Formatter.formatFileSize(getApplicationContext(), progress.currentSize);
                        String totalLength = Formatter.formatFileSize(getApplicationContext(), progress.totalSize);
                        String speed = Formatter.formatFileSize(getApplicationContext(), progress.speed);
                        Log.e("OkGo", "下载速度：" + speed + "--已经下载：" + downloadLength + "--整个文件大小：" + totalLength);
                    }
                });
    }

    private void handleError(Response<File> response) {

    }

    private String getUrl(String clipboardContentTest) {
        String url = "http" + clipboardContentTest.split("http")[1].split("复制此链接")[0];
        Log.e("复制内容：", url);
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

    }


}
