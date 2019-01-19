package com.zt.task.system.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zt.task.system.util.ToastUtil;

import java.util.List;


/**
 * 线程保活心跳
 */
public class TickBroadcastReceiver extends BroadcastReceiver {

    private ActivityManager manager;

    public TickBroadcastReceiver(ActivityManager manager) {
        this.manager = manager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
            checkServiceRunning(context);
        }
    }

    private void checkServiceRunning(Context context) {
        boolean isServiceRunning = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> RunningServiceInfo = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : RunningServiceInfo) {
//            ToastUtil.showShort(context, "ticked 服务检测");
            if ("com.zt.task.system.core.CommandService".equals(service.service.getClassName())) {
                isServiceRunning = true;
            }
        }
        if (!isServiceRunning) {
            ToastUtil.showShort(context, "main线程退出");
//            Intent i = new Intent(context, CommandService.class);
//            context.startService(i);
        }
    }
}
