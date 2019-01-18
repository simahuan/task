package com.zt.task.system.service;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.zt.task.system.APP;
import com.zt.task.system.util.LauncherUtils;
import com.zt.task.system.util.LogUtils;
import com.zt.task.system.util.ToastUtil;


/**
 * Created by Administrator on 2016/7/29.
 */
public class MyAccessibilityService extends BaseAccessibilityService {
    private static final String TAG = "MyAccessibilityService";

    public static int taskCount = APP.getInstance().getAmount();

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message pMessage) {
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
//                    Log.e(TAG, "没有事件触发");
                    break;
            }
            return false;
        }
    });

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onAccessibilityEvent(accessibilityEvent);
        if (accessibilityEvent == null) {
            Log.e(TAG, "没有事件触发");
            return;
        }
        int eventType = accessibilityEvent.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Log.v(TAG, "typeWindowContentChanged");
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                if (accessibilityEvent.getClassName().equals("com.baidu.appsearch.MainActivity")) {
                    AccessibilityNodeInfo nodeInfo = findViewByText("分类", true);
                    if (nodeInfo != null) {
                        performViewClick(nodeInfo);
                        APP app = APP.getInstance();
                        if (null != app && null != app.getTaskType()) {
                            if ("刷词".equalsIgnoreCase(APP.getInstance().getTaskType().trim())) {
                                mHandler.sendEmptyMessage(1);
                            } else {
                                ToastUtil.showShort(this, "刷词以外其它类型任务开发中..");
                            }
                        } else {
                            ToastUtil.showShort(this, "获取任务类型失败");
                        }
                    }
                    Log.v(TAG, "typeWindowStateChanged");
                    break;
                }
        }
    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.packageNames = new String[]{"com.baidu.appsearch"};
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.notificationTimeout = 2000;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
        Log.e(TAG, "onServiceConnected");

        LauncherUtils.clearPackage("com.baidu.appsearch");
        LauncherUtils.launchAPK3(this, "com.baidu.appsearch");

    }

    @Override
    public void onInterrupt() {
        LogUtils.d("onInterrupt");
    }

    private void stepOneFindSearchBoxClick() {
        postedDelayExecute(5);
        AccessibilityNodeInfo nodeInfo = findViewByID("com.baidu.appsearch:id/libui_titlebar_search_box");
        if (null != nodeInfo) {
            Log.e(TAG, "收到第一步任务 ");
//            nodeInfo.recycle();
//            mHandler.sendEmptyMessage(2);
            boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Log.e(TAG, "the click result: " + result);
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
        AccessibilityNodeInfo nodeInfo = findViewByID("com.baidu.appsearch:id/search_result_search_textinput");
        if (nodeInfo == null) {
            Log.e(TAG, "nodeInfo  is null: ");
            return;
        }
        boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        Log.e(TAG, "focus result: " + result);
        inputText(nodeInfo, getKeyWords());
        nodeInfo.recycle();
        mHandler.sendEmptyMessage(5);
    }

    /**
     * 长尾关键词 循环
     */
    private void stepFiveLongTailKeyWords() {
        Log.e(TAG, "收到长尾关键词任务 ");
        postedDelayExecute(5);
        AccessibilityNodeInfo nodeInfo = findViewByID("com.baidu.appsearch:id/search_result_search_textinput");
        if (nodeInfo == null) {
            Log.e(TAG, "nodeInfo  is null: ");
            return;
        }
        boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        Log.e(TAG, "focus result: " + result);
        inputText(nodeInfo, getProductName());
        nodeInfo.recycle();
        mHandler.sendEmptyMessage(3);
    }

    private void stepThreeExecuteSearchTask() {
        postedDelayExecute(5);
        AccessibilityNodeInfo nodeInfo = findViewByID("com.baidu.appsearch:id/search_result_search");
        if (nodeInfo == null) {
            Log.e(TAG, "stepThreeExecuteSearchTask  nodeInfo  is null: ");
            return;
        }
        boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        Log.e(TAG, "the click result: " + result);
        if (result) {
            nodeInfo.recycle();
            Log.e(TAG, "指令结束退出操作  ");
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