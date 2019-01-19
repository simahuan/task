package com.zt.task.system.service;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.zt.task.system.util.Constant;
import com.zt.task.system.util.LogUtils;
import com.zt.task.system.util.Preferences;
import com.zt.task.system.util.ShellUtils;
import com.zt.task.system.util.ToastUtil;
import com.zt.task.system.ztApplication;


/**
 * Created by Administrator on 2016/7/29.
 */
public class MyAccessibilityService extends BaseAccessibilityService {
    private static final String TAG = "MyAccessibilityService";

    public static int taskCount = ztApplication.getInstance().getAmount();


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message pMessage) {
            if (Preferences.getBoolean(MyAccessibilityService.this, Constant.KEY_TASK_ERROR)) {
                performHomeClick();
                return false;
            }
            switch (pMessage.what) {
                case 1:
                    stepOneFindSearchBoxClick();
                    break;
                case 2:
                    stepTwoInputKeyWords();
                    break;
                case 3:
                    stepThreeExecuteSearchTask();
                    break;
                case 4:
                    stepFourExecuteDownload();
                    break;
                case 5:
                    stepFiveLongTailKeyWords();
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onAccessibilityEvent(accessibilityEvent);
        if (accessibilityEvent == null) {
            return;
        }
        int eventType = accessibilityEvent.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                LogUtils.v("typeWindowContentChanged");
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                if (accessibilityEvent.getClassName().equals("com.baidu.appsearch.MainActivity")) {
                    AccessibilityNodeInfo nodeInfo = findViewByText("分类", true);
                    if (nodeInfo != null) {
                        performViewClick(nodeInfo);
                        String type;
                        ztApplication app = ztApplication.getInstance();
                        if (null != app && null != app.getTask()) {
                            type = app.getTaskType();
                        } else {
                            type = Preferences.getString(this, Constant.KEY_TASK_TYPE);
                        }
                        LogUtils.e("type=" + type);
                        if (null != type) {
                            if ("刷词".equalsIgnoreCase(type)) {
                                mHandler.sendEmptyMessage(1);
                            } else {
                                performHomeClick();
                                LogUtils.e("刷词以外其它类型任务开发中..");
                                ToastUtil.showShort(this, "刷词以外其它类型任务开发中..");
                            }
                        } else {
                            LogUtils.e("获取任务类型失败");
                            ToastUtil.showShort(this, "获取任务类型失败");
                        }
                    }
                    LogUtils.v("typeWindowStateChanged");
                    break;
                }
        }
    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.packageNames = new String[]{"com.baidu.appsearch"};
        info.flags = AccessibilityServiceInfo.DEFAULT
                | AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
                | AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        info.notificationTimeout = 2000;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
        LogUtils.e("onServiceConnected");
        Preferences.set(ztApplication.getAppContext(),Constant.KEY_ACCESSIBILITY_SERVICE_TAG,true);
//        LauncherUtils.clearPackage("com.baidu.appsearch");
//        postedDelayExecute(2);
//        Preferences.set(getBaseContext(), Constant.KEY_TASK_INIT_NOT_START, false);
//        LauncherUtils.launchAPK1(ztApplication.getAppContext(), "com.baidu.appsearch");
    }

    @Override
    public void onInterrupt() {
        LogUtils.e("onInterrupt");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.e("onUnbind");
        Preferences.set(ztApplication.getAppContext(),Constant.KEY_ACCESSIBILITY_SERVICE_TAG,false);
        return super.onUnbind(intent);
    }

    private void stepOneFindSearchBoxClick() {
        postedDelayExecute(5);
        AccessibilityNodeInfo nodeInfo = findViewByID("com.baidu.appsearch:id/libui_titlebar_search_box");
        if (null != nodeInfo) {
            LogUtils.e("收到第一步任务 ");
            boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            LogUtils.e("the click result: " + result);
            if (result) {
                nodeInfo.recycle();
                mHandler.sendEmptyMessage(2);
                return;
            }
        }
    }

    private void stepTwoInputKeyWords() {
        Log.e(TAG, "收到第二步任务,输入关键词 ");
        postedDelayExecute(5);
        AccessibilityNodeInfo nodeInfo = findViewByID2("com.baidu.appsearch:id/search_result_search_textinput");
        if (nodeInfo == null) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", getKeyWords());
            String cmd = "sleep 2;input touchscreen swipe 205 80 205 80 2000;sleep 1;input tap 275 80;";
            ShellUtils.execCommand(cmd, true);
            clipboard.setPrimaryClip(clip);
        } else {
            boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            LogUtils.e("focus result: " + result);
            inputText(nodeInfo, getKeyWords());
            nodeInfo.recycle();
        }
        mHandler.sendEmptyMessage(5);
    }

    /**
     * 长尾关键词 循环
     */
    private void stepFiveLongTailKeyWords() {
        LogUtils.e("收到长尾关键词任务 ");
        postedDelayExecute(5);
        AccessibilityNodeInfo nodeInfo = findViewByID2("com.baidu.appsearch:id/search_result_search_textinput");

        if (nodeInfo == null) {
            LogUtils.e("nodeInfo  is null: 找不到输入焦点,继续执行 input text 输入");
            String cmd = "input tap 205 80; sleep 2;input text" + getProductName();
            ShellUtils.execCommand(cmd, true);
            mHandler.sendEmptyMessage(3);
            return;
        } else {
            boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            LogUtils.e("focus result: " + result);
            inputText(nodeInfo, getProductName());
            nodeInfo.recycle();
            mHandler.sendEmptyMessage(3);
            return;
        }
    }

    private void stepThreeExecuteSearchTask() {
        postedDelayExecute(5);
        AccessibilityNodeInfo nodeInfo = findViewByID("com.baidu.appsearch:id/search_result_search");
        if (nodeInfo == null) {
            LogUtils.e("stepThreeExecuteSearchTask  nodeInfo  is null: ");
            return;
        }
        boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        LogUtils.e("the click result: " + result);
        if (result) {
            nodeInfo.recycle();
            LogUtils.e("指令结束退出操作  ");
            postedDelayExecute(10);
            MyIntentService.startActionFoo(getBaseContext(), --taskCount, 0);
            performHomeClick();
            return;
        }
    }


    private void stepFourExecuteDownload() {
        postedDelayExecute(5);
        AccessibilityNodeInfo nodeInfo = findViewByID("com.baidu.appsearch:id/search_result_search");
        if (nodeInfo == null) {
            return;
        }

        if (nodeInfo.getText() != null && nodeInfo.getText().toString().contains("金道贵金属")) {
            if ("金道贵金属".equals(nodeInfo.getText().toString()) && "android.widget.TextView".equals(nodeInfo.getClassName())) {
                AccessibilityNodeInfo parent = nodeInfo;
                while (parent != null) {
                    if (parent.isClickable()) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        break;
                    }
                    parent = parent.getParent();
                }
            }
        }
    }


}