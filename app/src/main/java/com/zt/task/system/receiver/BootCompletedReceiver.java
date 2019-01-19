package com.zt.task.system.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zt.task.system.core.CommandService;
import com.zt.task.system.util.ToastUtil;


public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ToastUtil.showShort(context, "收到开机广播");
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, CommandService.class);
            context.startService(i);
        }
    }
}
