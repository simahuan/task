package com.zt.task.system.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.TextView;

import com.zt.task.system.util.LogUtils;


public class MainActivity extends Activity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        TextView tv = new TextView(this);
        tv.setText("展示页");
        tv.setTextSize(20);
        setContentView( tv);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.e("onRestart i启动...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.e("onStart i启动...");
        startService(new Intent(this,CommandService.class));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LogUtils.e("低内存告警");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e("Activity 退出");
    }
}
