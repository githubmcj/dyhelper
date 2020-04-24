package com.clearlee.autosendwechatmsg;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clearlee.autosendwechatmsg.common.WeChatTextWrapper;
import com.tbruyelle.rxpermissions.RxPermissions;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.clearlee.autosendwechatmsg.service.AutoSendMsgService.step;
import static com.clearlee.autosendwechatmsg.service.AutoSendMsgService.maxNum;

/**
 * Created by Clearlee
 * 2017/12/22.
 */
public class MainActivity extends AppCompatActivity {

    private TextView start;
    private EditText etMaxNum;
    private AccessibilityManager accessibilityManager;
    private static final int REQUEST_PERMISSION_STORAGE = 0x01;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        start = (TextView) findViewById(R.id.testWechat);
        etMaxNum = (EditText) findViewById(R.id.max_num);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndStartService();
            }
        });
        checkSDCardPermission();
    }


    /** 检查SD卡权限 */
    protected void checkSDCardPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        start.setVisibility(View.VISIBLE);
                    } else {
                        start.setVisibility(View.GONE);
                        Toast.makeText(this, "权限被禁止，无法下载文件！", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void goDouyin() {
        Intent intent = new Intent();
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(WeChatTextWrapper.DOUYIN_PACKAGENAME, WeChatTextWrapper.DouyinClass.DOUYIN_CLASS_LAUNCHUI);
        startActivity(intent);
        step = 0;
        maxNum = Integer.valueOf(etMaxNum.getText().toString());
    }


    private void openService() {
        try {
            //打开系统设置中辅助功能
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "找到抖音助手，然后开启服务即可", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAndStartService() {
        accessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        if (!accessibilityManager.isEnabled()) {
            openService();
        } else {
            goDouyin();
        }
    }
}
