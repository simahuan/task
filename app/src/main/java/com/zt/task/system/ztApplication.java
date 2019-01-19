package com.zt.task.system;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.tencent.bugly.crashreport.CrashReport;
import com.zt.task.system.entity.Task;
import com.zt.task.system.service.BaseAccessibilityService;
import com.zt.task.system.util.Constant;
import com.zt.task.system.util.LogUtils;
import com.zt.task.system.util.ParcelableUtil;
import com.zt.task.system.util.Preferences;


/**
 *
 */
public class ztApplication extends Application {
    private String TAG = ztApplication.class.getSimpleName();
    private volatile static ztApplication instance = null;
    private ActivityManager mActivityManager;
    private static int task_count = 0;
    private Task mTask;
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        initTask();
        printLog();
    }

    public static Context getAppContext() {

        return sContext;
    }

    public ActivityManager getActivityManager() {
        return mActivityManager;
    }

    private void initTask() {
        CrashReport.initCrashReport(getApplicationContext(), "cd4ba6552d", false);
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        BaseAccessibilityService.getInstance().init(this);
        Preferences.set(ztApplication.getAppContext(), Constant.KEY_ACCESSIBILITY_SERVICE_TAG, false);
//        Preferences.set(getBaseContext(), Constant.KEY_TASK_STATUS, Constant.TASK_IDLE);
        LogUtils.e("Application initTask");
    }

    private void printLog() {
        printConsoleLog();
        printDiskLog();
    }

    public static ztApplication getInstance() {
        if (instance == null) {
            synchronized (ztApplication.class) {
                if (instance == null) {
                    instance = new ztApplication();
                }
            }
        }
        return instance;
    }

    private void printConsoleLog() {
        /**
         *  以下是打印自定义日志到控制台
         */
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag(TAG)   // 自定义TAG全部标签，默认PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

    private void printDiskLog() {
        /**
         * 打印本地磁盘
         */
        FormatStrategy formatStrategyTwo = CsvFormatStrategy.newBuilder()
                .tag(TAG)
                .build();
        Logger.addLogAdapter(new DiskLogAdapter(formatStrategyTwo));
    }


    public int getTaskCount() {
        return task_count;
    }

    /**
     * 任务要进行清零操任务
     *
     * @param pTask_count
     */
    public void setTaskCount(int pTask_count) {
        this.task_count = pTask_count;
    }

    public void setTask(Task pTask) {
        this.mTask = pTask;
    }

    public Task getTask() {
        return mTask;
    }

    /**
     * 获取任务类型
     *
     * @return
     */
    public String getTaskType() {
        return mTask != null ? mTask.getType() : null;
    }

    public int getAmount() {
        int mount = -1;

        if (null == mTask) {
            byte[] b = Preferences.getBytes(sContext, Constant.KEY_TASK_BEAN);
            Task task = ParcelableUtil.unmarshal(b, Task.CREATOR);
            mount = task != null ? task.getAmount() : -10000;
        }
        if (null != mTask) {
            mount = mTask.getAmount();
            return mount;
        }
        return mount;
    }
}
