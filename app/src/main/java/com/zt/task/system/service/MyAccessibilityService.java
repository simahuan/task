package com.zt.task.system.service;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.accessibility.AccessibilityEvent;

import com.zt.task.system.entity.Task;
import com.zt.task.system.util.Constant;
import com.zt.task.system.util.LogUtils;
import com.zt.task.system.util.ParcelableUtil;
import com.zt.task.system.util.Preferences;
import com.zt.task.system.util.ShellUtils;
import com.zt.task.system.util.ToastUtil;
import com.zt.task.system.ztApplication;

import org.greenrobot.eventbus.EventBus;


/**
 * @author Created by Administrator on 2016/7/29.
 */
public class MyAccessibilityService extends BaseAccessibilityService {
    public interface ExecuteType {
        /**
         * 应用刷词
         */
        String TYPE_BRUSH_WORD = "刷词";
        /**
         * 应用下载
         */
        String TYPE_DOWNLOAD = "下载";
        /**
         * 应用评论
         */
        String TYPE_COMMENT = "评论";

        /**
         * 安装 apk
         */
        String TYPE_INSTALL = "安装";
    }


    ExecuteStrategy mExecuteStrategy;
    AccessibilityEvent accessibilityEvent;

    public void setStrategy(ExecuteStrategy pStrategy) {
        this.mExecuteStrategy = pStrategy;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent == null) {
            return;
        }
        int eventType = accessibilityEvent.getEventType();
        this.accessibilityEvent = accessibilityEvent;
        LogUtils.e("eventType:" + Integer.toHexString(eventType));
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                LogUtils.e("TYPE_WINDOW_CONTENT_CHANGED");
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                LogUtils.e("TYPE_WINDOW_STATE_CHANGED");
                // 任务正在执行中，空闲 两种状态监听
                if (Preferences.getInt(getBaseContext(), Constant.KEY_TASK_STATUS) == 1) {
                    if (Preferences.getBoolean(getBaseContext(), Constant.KEY_COMMENT_REGISTER)) {
                        // 帐户已经完成注册
                        LogUtils.e("帐户已经完成注册");
                    } else {
                        dispatchAppStrategy(accessibilityEvent);
                    }
                } else {
                    LogUtils.e("task_status=" + Preferences.getInt(getBaseContext(), Constant.KEY_TASK_STATUS));
                }
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                //notice message
                LogUtils.e("TYPE_NOTIFICATION_STATE_CHANGED");
                if (null != mExecuteStrategy) {
                    app = ztApplication.getInstance();
                    if (null != app && null != app.getTask()) {
                        type = app.getTaskType();
                    } else {
                        type = Preferences.getString(mContext, Constant.KEY_TASK_TYPE);
                    }
                    if ("下载".equals(type) || "评论".equals(type)) {
                        mExecuteStrategy.executeType(ExecuteStrategy.TYPE_INSTALL);
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                LogUtils.e("TYPE_WINDOWS_CHANGED");
                break;
            default:
                break;
        }
        super.onAccessibilityEvent(accessibilityEvent);
    }

    String type = null;
    ztApplication app;

    /**
     * 派发应用策略
     *
     * @param accessibilityEvent
     */
    private void dispatchAppStrategy(AccessibilityEvent accessibilityEvent) {
        app = ztApplication.getInstance();
        Task task = app.getTask();
        if (null != app && null != task) {
            type = app.getTaskType();
        } else {
            type = Preferences.getString(mContext, Constant.KEY_TASK_TYPE);
        }
        LogUtils.e("type=" + type);
        if (null != type && null != mExecuteStrategy && isAppLaunchHome(accessibilityEvent)) {
            if (ExecuteType.TYPE_BRUSH_WORD.equalsIgnoreCase(type)) {
                mExecuteStrategy.executeType(ExecuteStrategy.TYPE_BRUSH_WORD);
            } else if (ExecuteType.TYPE_DOWNLOAD.equalsIgnoreCase(type)) {
                String pkgName = task.getProductPackage().trim().toLowerCase();
                if (isPkgInstalled(pkgName)) {
                    postedUninstallExecute(pkgName);
                }
                mExecuteStrategy.executeType(ExecuteStrategy.TYPE_APP_DOWNLOAD);
            } else if (ExecuteType.TYPE_COMMENT.equalsIgnoreCase(type)) {
                mExecuteStrategy.executeType(ExecuteStrategy.TYPE_COMMENT);
            } else {
                performHomeClick();
                LogUtils.e("三大類以外其它类型任务开发中..");
                ToastUtil.showShort(mContext, "三大類以外其它类型任务开发中..");
            }
        } else {
            LogUtils.e("任务不执行：isLaunchHome=" + isAppLaunchHome(accessibilityEvent) + ",type=" + type);
            ToastUtil.showShort(mContext, "app is not launch home");
        }
    }

    /**
     * 应用启动首页
     *
     * @param accessibilityEvent
     * @return 是否停留启动首页
     */
    private boolean isAppLaunchHome(AccessibilityEvent accessibilityEvent) {
        return "com.baidu.appsearch.MainActivity".equals(accessibilityEvent.getClassName());
    }

    private boolean isPkgInstalled(String pkgName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        } finally {
            if (packageInfo == null) {
                return false;
            } else {
                return true;
            }
        }
    }


    public void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    public void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onServiceConnected() {
        byte[] b = Preferences.getBytes(this, Constant.KEY_TASK_BEAN);
        Task task = ParcelableUtil.unmarshal(b, Task.CREATOR);
        LogUtils.e("onServiceConnected,appMarket = " + task.getAppMarket() + "\n" + Preferences.getString(this, Constant.KEY_TASK_MARKET));

        AccessibilityServiceInfo info = getServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.flags = AccessibilityServiceInfo.DEFAULT
                | AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
                | AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
                | AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
        info.notificationTimeout = 1000;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);

        setStrategy(new BaiDuAppSearchStrategy(this));
        Preferences.set(ztApplication.getAppContext(), Constant.KEY_ACCESSIBILITY_SERVICE_TAG, true);
        super.onServiceConnected();
    }

    @Override
    public void onInterrupt() {
        LogUtils.e("onInterrupt");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.e("onUnbind");
        Preferences.set(ztApplication.getAppContext(), Constant.KEY_ACCESSIBILITY_SERVICE_TAG, false);
        return super.onUnbind(intent);
    }

    /**
     * 处理删除指定apk
     *
     * @param pkgName
     */
    public void postedUninstallExecute(String pkgName) {
        String cmd = "pm  uninstall " + pkgName + ";";
        ShellUtils.execCommand(cmd, true);
    }
}