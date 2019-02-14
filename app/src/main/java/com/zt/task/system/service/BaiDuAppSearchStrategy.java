package com.zt.task.system.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import com.zt.task.system.util.Constant;
import com.zt.task.system.util.LogUtils;
import com.zt.task.system.util.Preferences;
import com.zt.task.system.util.ShellUtils;
import com.zt.task.system.ztApplication;

import java.util.List;

/**
 * @author
 */
public class BaiDuAppSearchStrategy implements ExecuteStrategy {

    private boolean isFirstScreenProduceName = true;

    interface ExecuteStep {
        int STEP_ONE_FIND_SEARCH_BOX = 0x1;
        int STEP_TWO_INPUT_KEYWORDS = 0x2;
        int STEP_THREE_EXECUTE_SEARCH_RESULT = 0x3;
        int STEP_FIVE_EXECUTE_DOWNLOAD = 0x5;
        int STEP_FOUR_EXECUTE_LONG_TAIL_WORDS = 0x4;
    }

    private Handler brushWordHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message pMessage) {
            if (Preferences.getBoolean(mContext, Constant.KEY_TASK_ERROR)) {
                baseAccessService.performHomeClick();
                return false;
            }
            switch (pMessage.what) {
                case ExecuteStep.STEP_ONE_FIND_SEARCH_BOX:
                    stepOneFindSearchBoxClick();
                    break;
                case ExecuteStep.STEP_TWO_INPUT_KEYWORDS:
                    stepTwoInputKeyWords();
                    break;
                case ExecuteStep.STEP_THREE_EXECUTE_SEARCH_RESULT:
                    stepThreeExecuteSearchTask();
                    break;
                case ExecuteStep.STEP_FOUR_EXECUTE_LONG_TAIL_WORDS:
                    stepFourLongTailKeyWords();
                    break;
                case ExecuteStep.STEP_FIVE_EXECUTE_DOWNLOAD:
                    stepFiveExecuteDownload();
                    break;

                default:
                    break;
            }
            return true;
        }
    });

    private Handler downloadHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (Preferences.getBoolean(mContext, Constant.KEY_TASK_ERROR)) {
                baseAccessService.performHomeClick();
                return false;
            }
            switch (msg.what) {
                case ExecuteStep.STEP_ONE_FIND_SEARCH_BOX:
                    stepOneFindSearchBoxClick();
                    break;
                case ExecuteStep.STEP_TWO_INPUT_KEYWORDS:
                    stepTwoInputKeyWords();
                    break;
                case ExecuteStep.STEP_THREE_EXECUTE_SEARCH_RESULT:
                    stepThreeExecuteSearchTask();
                    break;
                case ExecuteStep.STEP_FIVE_EXECUTE_DOWNLOAD:
                    stepFiveExecuteDownload();
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    private Context mContext;
    private MyAccessibilityService baseAccessService;

    public BaiDuAppSearchStrategy(MyAccessibilityService pBaseAccessService) {
        this.mContext = ztApplication.getAppContext();
        this.baseAccessService = pBaseAccessService;
    }


    @Override
    public void executeType(int type) {
        switch (type) {
            case TYPE_BRUSH_WORD:
                LogUtils.e("执行刷词");
                brushWord();
                break;
            case TYPE_APP_DOWNLOAD:
                LogUtils.e("执行下载");
                download();
                break;
            case TYPE_COMMENT:
                LogUtils.e("执行评论");
                break;
            case TYPE_INSTALL:
                LogUtils.e("启动应用安装监听.....");
                installApk();
                break;
            default:
                LogUtils.e("执行 default  策略");
                break;
        }
    }

    private boolean installHome = false;

    private void installApk() {
        AccessibilityNodeInfo rootNode = baseAccessService.getRootInActiveWindow();
        if (null == rootNode) {
            return;
        }
        List<AccessibilityNodeInfo> nodeInfos = rootNode.findAccessibilityNodeInfosByText("金道贵金属");
        if (null == nodeInfos && nodeInfos.isEmpty()) {
            LogUtils.e("金道贵金属,未到此页面");
            return;
        }
        for (AccessibilityNodeInfo n : nodeInfos) {
            CharSequence resourceName = n.getPackageName();
            CharSequence className = n.getClassName();
            if (null != n && "com.android.packageinstaller".equals(resourceName)
                    && "android.widget.TextView".equals(className)) {
                installHome = true;
            }
        }

        if (!installHome) {
            return;
        }

        LogUtils.e("开始进入下一步");
        List<AccessibilityNodeInfo> okButtons = rootNode.findAccessibilityNodeInfosByText("下一步");
        if (null != okButtons && !okButtons.isEmpty()) {
            AccessibilityNodeInfo node = okButtons.get(0);
            if (null != node) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
        LogUtils.e("开始进入 安装");
        baseAccessService.postedDelayExecute(5);
        List<AccessibilityNodeInfo> installButtons = rootNode.findAccessibilityNodeInfosByText("安装");
        if (null != installButtons && !installButtons.isEmpty()) {
            for (AccessibilityNodeInfo installBtn : installButtons) {
                if ("安装".equals(installBtn.getText())) {
                    installBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }

        baseAccessService.postedDelayExecute(15);
        List<AccessibilityNodeInfo> installing = rootNode.findAccessibilityNodeInfosByText("正在安装...");
        if (null != installing && !installing.isEmpty()) {
            LogUtils.e("继续待待安装10s");
            baseAccessService.postedDelayExecute(10);
        }

        List<AccessibilityNodeInfo> installed = rootNode.findAccessibilityNodeInfosByText("应用安装完成。");
        if (null != installed && !installed.isEmpty()) {
            LogUtils.e("Apk安装已经完成");
        }
        LogUtils.e("开始进入 完成");
        baseAccessService.postedDelayExecute(5);
        List<AccessibilityNodeInfo> finishButtons = baseAccessService.getRootInActiveWindow().findAccessibilityNodeInfosByText("完成");
        if (null != finishButtons && !finishButtons.isEmpty()) {
            for (AccessibilityNodeInfo finishBtn : finishButtons) {
                if ("完成".equals(finishBtn.getText())) {
                    finishBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
        LogUtils.e("完成安装流程，返回桌面,上报数据");
        baseAccessService.postedDelayExecute(5);
        installHome = false;
        baseAccessService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    /**
     * 刷词入口
     */
    private void brushWord() {
        AccessibilityNodeInfo nodeInfo = baseAccessService.findViewByText("分类", true);
        if (nodeInfo != null) {
            baseAccessService.performViewClick(nodeInfo);
            brushWordHandler.sendEmptyMessage(ExecuteStep.STEP_ONE_FIND_SEARCH_BOX);
        }
    }

    /**
     * app 下载入口
     */
    private void download() {
        AccessibilityNodeInfo nodeInfo = baseAccessService.findViewByText("分类", true);
        if (nodeInfo != null) {
            baseAccessService.performViewClick(nodeInfo);
        }

        //---
        baseAccessService.postedDelayExecute(5);
        nodeInfo = baseAccessService.findViewByID("com.baidu.appsearch:id/libui_titlebar_search_box");
        if (null != nodeInfo) {
            LogUtils.e("收到第一步任务 .click search box");
            boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            LogUtils.e("the click result: " + result);
        }

        //----
        LogUtils.e("收到第二步任务,输入下载产品名称 ");
        baseAccessService.postedDelayExecute(5);
        nodeInfo = baseAccessService.findViewByID2("com.baidu.appsearch:id/search_result_search_textinput");
        if (nodeInfo == null) {
            String cmd = "sleep 2;input touchscreen swipe 205 80 205 80 2000;sleep 1;input tap 275 80; input text " + baseAccessService.getProductName();
            ShellUtils.execCommand(cmd, true);
        } else {
            boolean result = nodeInfo.performAction(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY);
            LogUtils.e("focus result: " + result);
            baseAccessService.inputText(nodeInfo, baseAccessService.getProductName());
            nodeInfo.recycle();
        }

        //----
        //        baseAccessService.postedDelayExecute(3);
        LogUtils.e("收到第三步任务,执行点击搜索产品名称 ");
        nodeInfo = baseAccessService.findViewByID("com.baidu.appsearch:id/search_result_search");
        if (nodeInfo == null) {
            LogUtils.e("stepThreeExecuteSearchTask  nodeInfo  is null: ");
            return;
        }
        boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        LogUtils.e("the click result: " + result);
        nodeInfo.recycle();
        LogUtils.e("搜索 结果等待 时间 ");
        baseAccessService.postedDelayExecute(20);

        //-------download
        AccessibilityNodeInfo rootNode = baseAccessService.getRootInActiveWindow();
        if (null == rootNode) {
            return;
        }
        List<AccessibilityNodeInfo> loadingNodes = rootNode.findAccessibilityNodeInfosByViewId("com.baidu.appsearch:id/loading_imageView");
        if (null != loadingNodes && !loadingNodes.isEmpty()) {
            LogUtils.e("搜索 结果 转圈 继续等待 20s ");
            baseAccessService.postedDelayExecute(20);
        }

        LogUtils.e("收到第四步任务,执行下载产品名称 ");
        findAndClickDownloadProduce(rootNode);

        if (!isFirstScreenProduceName) {
            // 建议滚屏 --向上滚动 --- 递归调用
            LogUtils.e("建议滚屏 --向上滚动 --- 递归调用");
            List<AccessibilityNodeInfo> nInfos = rootNode.findAccessibilityNodeInfosByViewId("com.baidu.appsearch:id/recyclerview");
            AccessibilityNodeInfo mNode = nInfos.get(0);
            if (null != mNode) {
                mNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            }
            findAndClickDownloadProduce(rootNode);
        }
    }

    private void findAndClickDownloadProduce(AccessibilityNodeInfo pRootNode) {
        List<AccessibilityNodeInfo> appItemNodes = pRootNode.findAccessibilityNodeInfosByViewId("com.baidu.appsearch:id/app_item");
        for (AccessibilityNodeInfo itemNodes : appItemNodes) {
            List<AccessibilityNodeInfo> appnames = itemNodes.findAccessibilityNodeInfosByViewId("com.baidu.appsearch:id/appitem_title");
            if (null != appnames && !appnames.isEmpty()) {
                for (AccessibilityNodeInfo appnameNode : appnames) {
                    // 首屏出现关键产品信息
                    if (TextUtils.equals(appnameNode.getClassName(), "android.widget.TextView")
                            && TextUtils.equals("金道贵金属", appnameNode.getText())) {
                        isFirstScreenProduceName = true;

                        List<AccessibilityNodeInfo> okNodes = itemNodes.findAccessibilityNodeInfosByViewId("com.baidu.appsearch:id/app_action");
                        for (AccessibilityNodeInfo node : okNodes) {
                            List<AccessibilityNodeInfo> n = node.findAccessibilityNodeInfosByText("下载");
                            List<AccessibilityNodeInfo> m = node.findAccessibilityNodeInfosByViewId("com.baidu.appsearch:id/text");
                            AccessibilityNodeInfo doneNode = n.get(0);
                            if (null != doneNode) {
                                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                break;
                            }
                        }
                    } else {
                        isFirstScreenProduceName = false;
                    }
                }
            }
        }
    }


    private void stepOneFindSearchBoxClick() {
        baseAccessService.postedDelayExecute(5);
        AccessibilityNodeInfo nodeInfo = baseAccessService.findViewByID("com.baidu.appsearch:id/libui_titlebar_search_box");
        if (null != nodeInfo) {
            LogUtils.e("收到第一步任务 ");
            boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            LogUtils.e("the click result: " + result);
            if (result) {
                nodeInfo.recycle();
                brushWordHandler.sendEmptyMessage(ExecuteStep.STEP_TWO_INPUT_KEYWORDS);
                return;
            }
        }
    }

    private void stepTwoInputKeyWords() {
        LogUtils.e("收到第二步任务,输入关键词 ");
        baseAccessService.postedDelayExecute(5);
        AccessibilityNodeInfo nodeInfo = baseAccessService.findViewByID2("com.baidu.appsearch:id/search_result_search_textinput");
        if (nodeInfo == null) {
            String cmd = "sleep 2;input touchscreen swipe 205 80 205 80 2000;sleep 1;input tap 275 80; input text " + baseAccessService.getKeyWords();
            ShellUtils.execCommand(cmd, true);
        } else {
            boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            LogUtils.e("focus result: " + result);
            baseAccessService.inputText(nodeInfo, baseAccessService.getKeyWords());
            nodeInfo.recycle();
        }

        if (baseAccessService.isLongTailsWords()) {
            brushWordHandler.sendEmptyMessage(ExecuteStep.STEP_FOUR_EXECUTE_LONG_TAIL_WORDS);
        } else {
            LogUtils.e("没有长尾关键词");
            brushWordHandler.sendEmptyMessage(ExecuteStep.STEP_THREE_EXECUTE_SEARCH_RESULT);
        }
    }

    /**
     * 执行搜索结果
     */
    private void stepThreeExecuteSearchTask() {
        baseAccessService.postedDelayExecute(3);
        AccessibilityNodeInfo nodeInfo = baseAccessService.findViewByID("com.baidu.appsearch:id/search_result_search");
        if (nodeInfo == null) {
            LogUtils.e("stepThreeExecuteSearchTask  nodeInfo  is null: ");
            return;
        }
        boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        LogUtils.e("the click result: " + result);
        nodeInfo.recycle();
        if (result) {
            LogUtils.e("指令结束退出操作  ");
            baseAccessService.postedDelayExecute(10);
            TaskIntentService.startActionReportTask(mContext, "");
            baseAccessService.performHomeClick();
            return;
        }
    }

    /**
     * 长尾关键词 循环
     */
    private void stepFourLongTailKeyWords() {
        LogUtils.e("收到长尾关键词任务 ");
        baseAccessService.postedDelayExecute(5);
        String[] longTails = baseAccessService.getLongTailWords();
        for (int i = 1; i < longTails.length; i++) {
            String words = longTails[i];
            LogUtils.e("words=" + words);
            execLongTails(words);
        }
        stepThreeExecuteSearchTask();
        return;
    }

    private void execLongTails(String tail) {
        baseAccessService.postedDelayExecute(5);
        AccessibilityNodeInfo nodeInfo = baseAccessService.findViewByID2("com.baidu.appsearch:id/search_result_search_textinput");
        if (nodeInfo == null) {
            LogUtils.e("nodeInfo  is null: 找不到输入焦点,继续执行 input text 输入：" + tail);
            String cmd = " sleep 2; input tap 205 80; sleep 2; input text " + tail;
            ShellUtils.execCommand(cmd, true);
        } else {
            boolean result = nodeInfo.performAction(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY);
            LogUtils.e("focus result: " + result);
            baseAccessService.inputText(nodeInfo, tail);
            nodeInfo.recycle();
        }
        return;
    }


    private void stepFiveExecuteDownload() {
        baseAccessService.postedDelayExecute(5);
        AccessibilityNodeInfo nodeInfo = baseAccessService.findViewByID("com.baidu.appsearch:id/search_result_search");
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
