package com.zt.task.system;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.tencent.bugly.crashreport.CrashReport;
import com.zt.task.system.entity.Task;


/**
 * @author ytf
 */
public class APP extends Application {
    private volatile static APP instance = null;//防止多个线程同时访问
    private static int task_count = 0;
    private Task mTask;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "cd4ba6552d", false);

        //      以下是打印自定义日志到控制台
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("MyCustomTag")   // 自定义TAG全部标签，默认PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
//      以下是保存自定义日志
        FormatStrategy formatStrateg = CsvFormatStrategy.newBuilder()
                .tag("custom")
                .build();
        Logger.addLogAdapter(new DiskLogAdapter(formatStrateg));

    }

    //应用层的单例模式
    public static APP getInstance() {
        if (instance == null) {
            synchronized (APP.class) {
                if (instance == null) {
                    instance = new APP();
                }
            }
        }
        return instance;
    }

    public int getTaskCount(){
        return  task_count;
    }

    /**
     * 任务要进行清零操任务
     * @param pTask_count
     */
    public void setTaskCount(int pTask_count){
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
     * @return
     */
    public String getTaskType(){
        return mTask != null ? mTask.getType() : null;
    }

    public int getAmount() {
        int mount = -1;
        if (null != mTask) {
            mount = mTask.getAmount();
            return mount;
        }
        return mount;
    }
}
