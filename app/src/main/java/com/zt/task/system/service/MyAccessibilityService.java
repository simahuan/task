package com.zt.task.system.service;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.zt.task.system.entity.Task;
import com.zt.task.system.util.Constant;
import com.zt.task.system.util.LogUtils;
import com.zt.task.system.util.ParcelableUtil;
import com.zt.task.system.util.Preferences;
import com.zt.task.system.util.ShellUtils;
import com.zt.task.system.util.ToastUtil;
import com.zt.task.system.ztApplication;


/**
 * @author
 * Created by Administrator on 2016/7/29.
 */
public class MyAccessibilityService extends BaseAccessibilityService {
    private static final String TAG = "MyAccessibilityService";

    public static int taskCount = ztApplication.getInstance().getAmount();
    public static boolean isLoopFinish = false;
    private AccessibilityEvent mAccessibilityEvent;

    protected enum TypeEnum {

    }

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
        mAccessibilityEvent = accessibilityEvent;
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                LogUtils.v("typeWindowContentChanged");
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                //整个应用控制中心
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
            default:
                break;
        }
    }

    @Override
    protected void onServiceConnected() {
        byte[] b = Preferences.getBytes(this, Constant.KEY_TASK_BEAN);
        Task task = ParcelableUtil.unmarshal(b, Task.CREATOR);
        LogUtils.e("onServiceConnected,appMarket = " + task.getAppMarket());

        AccessibilityServiceInfo info = getServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.packageNames = new String[]{"com.baidu.appsearch"};
        info.flags = AccessibilityServiceInfo.DEFAULT
                | AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
                | AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        info.notificationTimeout = 1000;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);

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
            String cmd = "sleep 2;input touchscreen swipe 205 80 205 80 2000;sleep 1;input tap 275 80; input text " + getKeyWords();
            ShellUtils.execCommand(cmd, true);
        } else {
            boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            LogUtils.e("focus result: " + result);
            inputText(nodeInfo, getKeyWords());
            nodeInfo.recycle();
        }


        if (isLongTailsWords()) {
            mHandler.sendEmptyMessage(5);
        } else {
            LogUtils.e("没有长尾关键词");
            isLoopFinish = true;
            mHandler.sendEmptyMessage(3);
        }
    }

    /**
     * 长尾关键词 循环
     */
    private void stepFiveLongTailKeyWords() {
        LogUtils.e("收到长尾关键词任务 ");
        postedDelayExecute(5);
        String[] longTails = getLongTailWords();
        for (int i = 1; i < longTails.length; i++) {
            String words = longTails[i];
            LogUtils.e("words=" + words);
            execLongTails(words);
        }
        stepThreeExecuteSearchTask();
        return;
    }

    private void execLongTails(String tail) {
        postedDelayExecute(5);
        AccessibilityNodeInfo nodeInfo = findViewByID2("com.baidu.appsearch:id/search_result_search_textinput");
        if (nodeInfo == null) {
            LogUtils.e("nodeInfo  is null: 找不到输入焦点,继续执行 input text 输入：" + tail);
            String cmd = " sleep 2; input tap 205 80; sleep 2; input text " + tail;
            ShellUtils.execCommand(cmd, true);
        } else {
            boolean result = nodeInfo.performAction(AccessibilityNodeInfo.FOCUS_INPUT);
            LogUtils.e("focus result: " + result);
            inputText(nodeInfo, tail);
            nodeInfo.recycle();
        }
        return;
    }

    private void stepThreeExecuteSearchTask() {
        postedDelayExecute(3);
        AccessibilityNodeInfo nodeInfo = findViewByID("com.baidu.appsearch:id/search_result_search");
        if (nodeInfo == null) {
            LogUtils.e("stepThreeExecuteSearchTask  nodeInfo  is null: ");
            return;
        }
        boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        LogUtils.e("the click result: " + result);
        nodeInfo.recycle();
        if (result ){
            isLoopFinish = false;
            LogUtils.e("指令结束退出操作  ");
            postedDelayExecute(10);
            TaskIntentService.startActionReportTask(this, "");
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