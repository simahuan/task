package com.zt.task.system.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.zt.task.system.entity.MessageEvent;
import com.zt.task.system.util.Constant;
import com.zt.task.system.util.LauncherUtils;
import com.zt.task.system.util.LogUtils;
import com.zt.task.system.util.Preferences;
import com.zt.task.system.util.ShellUtils;
import com.zt.task.system.ztApplication;

import org.greenrobot.eventbus.EventBus;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {
    private static final String TAG = MyIntentService.class.getSimpleName();
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.administrator.accessibilityservicedemo.action.FOO";
    private static final String ACTION_TASK = "action.task";

    private static final String EXTRA_TASK_COUNT = "extra_task_count";
    private static final String EXTRA_TASK_TAG = "extra_task_tag";

    public MyIntentService() {
        super("MyIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFoo(Context context, int taskCount, int taskTag) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_TASK_COUNT, taskCount);
        intent.putExtra(EXTRA_TASK_TAG, taskTag);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionTask(Context context, int taskCount, int taskTag) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_TASK);
        intent.putExtra(EXTRA_TASK_COUNT, taskCount);
        intent.putExtra(EXTRA_TASK_TAG, taskTag);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                int taskCount = intent.getIntExtra(EXTRA_TASK_COUNT, -1);
                int taskTag = intent.getIntExtra(EXTRA_TASK_TAG, -1);
                handleActionFoo(taskCount, taskTag);
            } else if (ACTION_TASK.equals(action)) {
                int taskCount = intent.getIntExtra(EXTRA_TASK_COUNT, -1);
                int taskTag = intent.getIntExtra(EXTRA_TASK_TAG, -1);
                handleActionTask(taskCount, taskTag);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(int taskCount, int taskTag) {
        reportTask(taskCount);
        int taskStatus = Preferences.getInt(this, Constant.KEY_TASK_STATUS);

        int largerCount = ztApplication.getInstance().getAmount();
        int executeCount = ztApplication.getInstance().getTaskCount();
        int lastCount = largerCount - executeCount;
        LogUtils.e("总任务数量：" + largerCount);
        LogUtils.e("剩余任务数量：" + lastCount);
        LogUtils.e("执行任务数量：" + executeCount);

        if (lastCount >= 1 && 1 == taskStatus) {
            LauncherUtils.clearPackage("com.baidu.appsearch");
            postedDelayExecute(2);
            LauncherUtils.launchAPK3(this, "com.baidu.appsearch");
            EventBus.getDefault().post(new MessageEvent("task_status", Constant.TASK_EXECUTE));
        } else if (2 == taskStatus) {
            LogUtils.e("任务取消.");
            EventBus.getDefault().post(new MessageEvent("task_status", Constant.TASK_CANCEL));
            Preferences.set(this, Constant.KEY_TASK_STATUS, Constant.TASK_IDLE);
        } else if (1 == taskStatus) {
            Preferences.set(MyIntentService.this, Constant.KEY_TASK_STATUS, Constant.TASK_COMPLETED);
            EventBus.getDefault().post(new MessageEvent("task_status", Constant.TASK_COMPLETED));
            LogUtils.e("任务全部完成...状态改变.");
        } else if (0 == taskStatus) {
//            EventBus.getDefault().post(new MessageEvent("task_status", Constant.TASK_ERROR));
            Preferences.set(this, Constant.KEY_TASK_ERROR, true);
            LogUtils.e("心跳0 处理空闲IDLE态，待接任务" + taskStatus + ";lastConut =" + lastCount);
        } else {
            LogUtils.e("其它情况..退出...taskStatus=" + taskStatus + ";lastConut =" + lastCount);
        }
    }

    private void reportTask(int taskCount) {
        int task_count = ztApplication.getInstance().getTaskCount();
        task_count++;
        ztApplication.getInstance().setTaskCount(task_count);
        LogUtils.e("task_count=" + task_count);
        Preferences.set(this, Constant.KEY_TASK_EXECUTE_STATISTICAL, task_count);
        long createTime = Preferences.getLong(this, Constant.KEY_TASK_CREATE_TIME);
        long currentTime = System.currentTimeMillis();
        LogUtils.e("executeMillis=" + (currentTime - createTime));
        int second = (int) ((currentTime - createTime) / 1000);
        Preferences.set(this, Constant.KEY_TASK_SPENT_TIME, second);
        LogUtils.e("executeSecond=" + second);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionTask(int taskCount, int taskTag) {
        LogUtils.e("接到任务执行 Enabled  director");
        String cmd = "settings put secure enabled_accessibility_services com.zt.task.system/com.zt.task.system.service.MyAccessibilityService";
        ShellUtils.execCommand(cmd, true);
        String cmd2 = "settings put secure accessibility_enabled  1";
        ShellUtils.execCommand(cmd2, true);
        postedDelayExecute(2);
        String cmd3 = "settings put secure enabled_accessibility_services com.zt.task.system/com.zt.task.system.service.MyAccessibilityService";
        ShellUtils.execCommand(cmd3, true);
        String cmd4 = "settings put secure accessibility_enabled  1";
        ShellUtils.execCommand(cmd4, true);

        boolean result = BaseAccessibilityService.getInstance().checkAccessibilityEnabled("com.zt.task.system/.service.MyAccessibilityService");
        LogUtils.e("AccessibilityService===result:" + result);
        if (result || Preferences.getBoolean(ztApplication.getAppContext(), Constant.KEY_ACCESSIBILITY_SERVICE_TAG)) {
            Preferences.set(this, Constant.KEY_TASK_SPENT_TIME, 0);
            Preferences.set(this, Constant.KEY_TASK_EXECUTE_STATISTICAL, 0);

            LauncherUtils.clearPackage("com.baidu.appsearch");
            postedDelayExecute(2);
            LauncherUtils.launchAPK1(ztApplication.getAppContext(), "com.baidu.appsearch");
        } else {
            BaseAccessibilityService.getInstance().goAccess();
        }
    }

    public void postedDelayExecute(long second) {
        String cmd = "sleep " + second + ";";
        ShellUtils.execCommand(cmd, true);
    }
}
