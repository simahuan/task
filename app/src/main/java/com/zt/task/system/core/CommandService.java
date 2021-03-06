package com.zt.task.system.core;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import com.zt.captcha.vpnlibrary.monitor.VpnServiceMonitor;
import com.zt.task.system.entity.Command;
import com.zt.task.system.entity.HeartBeatThree;
import com.zt.task.system.entity.HeartBeatTwo;
import com.zt.task.system.entity.HeartBeatZero;
import com.zt.task.system.entity.MessageEvent;
import com.zt.task.system.entity.Task;
import com.zt.task.system.exception.ExceptionEngine;
import com.zt.task.system.okhttp.MyDataCallBack;
import com.zt.task.system.okhttp.OkHTTPManger;
import com.zt.task.system.receiver.TickBroadcastReceiver;
import com.zt.task.system.service.TaskIntentService;
import com.zt.task.system.util.Constant;
import com.zt.task.system.util.DeviceInfoUtils;
import com.zt.task.system.util.GsonUtil;
import com.zt.task.system.util.LogUtils;
import com.zt.task.system.util.Preferences;
import com.zt.task.system.util.ShellUtils;
import com.zt.task.system.util.Spanny;
import com.zt.task.system.util.ToastUtil;
import com.zt.task.system.websocket.WsManager;
import com.zt.task.system.websocket.WsStatusListener;
import com.zt.task.system.ztApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.core.content.ContextCompat;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.ByteString;

/**
 * @author
 */
public class CommandService extends Service implements VpnServiceMonitor.VpnStateCallback {
    private final static String TAG = "CommandService";
    private WsManager wsManager;

    String heartbeat_zero;
    String heartbeat_one;
    String heartbeat_two;
    String heartbeat_three;
    //  服务重启，任务标记位清0
    private TickBroadcastReceiver mTickBroadcastReceiver;


    @Override
    public void onSystemReady() {

    }

    @Override
    public void onStartDone() {

    }

    @Override
    public void onStopDone() {

    }

    private void initVpnService() {
        VpnServiceMonitor.getInstance().initMonitor(this);
//        VpnServiceMonitor.getInstance().startMonitor(null);
        VpnServiceMonitor.getInstance().registerObserver(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("CommandService---onCreate---- 服务调用");
        registerEventBus();
        clearReportZero();
//        initVpnService();
        initWebSocketConnect();
        initHeartBeat();

        initTickBootReceiver();
//        registerTickBootReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.e("CommandService --onStartCommand---服务启动");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("ComanndeService   onDestrory......");
//        stopWebSocketConnect();
//        VpnServiceMonitor.getInstance().stopMonitor(this);
        unregisterEventBus();
        stopHeartBeat();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initTickBootReceiver() {
        ActivityManager manager = ztApplication.getInstance().getActivityManager();
        mTickBroadcastReceiver = new TickBroadcastReceiver(manager);
    }

    private void registerTickBootReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(mTickBroadcastReceiver, filter);
    }

    private void unregisterTickBootReceiver() {
        unregisterReceiver(mTickBroadcastReceiver);
    }

    private void initWebSocketConnect() {
        String ws = "ws://192.168.1.195:2345/?uid=%1$s";
        String webSocketAdd = String.format(ws, DeviceInfoUtils.getIMEI(getBaseContext()));
//        String webSocketAdd = String.format(BuildConfig.WS, DeviceInfoUtils.getIMEI(getBaseContext()));
        connectedRouter(webSocketAdd);
    }

    private void stopWebSocketConnect() {
        if (wsManager != null) {
            wsManager.stopConnect();
            wsManager = null;
        }
    }

    private void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    private void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    private void getTaskInfo(String url, String token) {
        OkHTTPManger.getInstance().getAsynBackStringWithoutParms(url, token, new MyDataCallBack() {
            @Override
            public void requestSuccess(Object result) {
                LogUtils.e("OkHTTPManger-----requestSuccess");
                EventBus.getDefault().post(result);
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtils.e("OkHTTPManger-----requestFailure");
                EventBus.getDefault().post(e.toString());
            }
        });
    }


    private WsStatusListener wsStatusListener = new WsStatusListener() {
        @Override
        public void onOpen(Response response) {
            super.onOpen(response);
            LogUtils.e("wsStatusListener-----onOpen======");
            LogUtils.e("服务器连接成功");
//            clearReportZero();
        }

        @Override
        public void onMessage(String text) {
            LogUtils.d("wsStatusListener-----onMessage");
            LogUtils.d(fromHtmlText("服务器任务:" + text) + "\n\n");

            if (!TextUtils.isEmpty(text)) {
                try {
                    JSONObject object = new JSONObject(text);
                    Command cmd = Command.parse(object);
                    Preferences.set(getBaseContext(), Constant.KEY_COMMAND_BEAN, cmd);
                    if (!TextUtils.isEmpty(cmd.getTokens())
                            && !TextUtils.isEmpty(cmd.getUrl())
                            && cmd.getStatus() == 1) {
                        Preferences.set(getBaseContext(), Constant.KEY_TASK_INIT_NOT_START, true);
                        sendMessageToRouter(getHeartBeatOne());
                        //任务刚接到
                        Preferences.set(getBaseContext(), Constant.KEY_TASK_STATUS, Constant.TASK_EXECUTE);

                        getTaskInfo(cmd.getUrl(), cmd.getTokens());
                        Preferences.set(getBaseContext(), Constant.KEY_TASK_CREATE_TIME, System.currentTimeMillis());
                    } else if (!TextUtils.isEmpty(cmd.getTokens())
                            && !TextUtils.isEmpty(cmd.getUrl())
                            && cmd.getStatus() == 2) {
                        Preferences.set(getBaseContext(), Constant.KEY_TASK_STATUS, Constant.TASK_CANCEL);
                        if (null != mHandler) {
                            mHandler.sendEmptyMessage(200);
                        }
                    }
                } catch (Exception pE) {
                    pE.printStackTrace();
                    ToastUtil.showShort(getBaseContext(), "command 命令解析出错" + pE.getMessage());
                }
            }
        }

        @Override
        public void onMessage(ByteString bytes) {
            LogUtils.d("WsManager-----onMessage");
        }

        @Override
        public void onReconnect() {
            super.onReconnect();
        }

        @Override
        public void onClosing(int code, String reason) {
            super.onClosing(code, reason);
            LogUtils.e("wsStatusListener----onClosing");
            if (wsManager != null) {
                wsManager.getWebSocket().close(1000, "byebye");
                wsManager = null;
            }
        }

        @Override
        public void onClosed(int code, String reason) {
            super.onClosed(code, reason);
            LogUtils.e("wsStatusListener----onClosed");
        }

        @Override
        public void onFailure(Throwable t, Response response) {
            super.onFailure(t, response);
            LogUtils.e("wsStatusListener-----onFailure");
            clearReportZero();
        }
    };

    /**
     * 清空上报数据
     */
    private void clearReportZero() {
        LogUtils.e("清空上报数据..task_status = 0..");
        Preferences.set(this, Constant.KEY_TASK_SPENT_TIME, 0);
        Preferences.set(this, Constant.KEY_TASK_EXECUTE_STATISTICAL, 0);
        Preferences.set(CommandService.this, Constant.KEY_TASK_STATUS, Constant.TASK_IDLE);
        Preferences.set(this, Constant.KEY_TASK_ERROR, false);
        Preferences.set(this, Constant.KEY_TASK_TYPE, "clean");

        Preferences.set(this, Constant.KEY_COMMENT_REGISTER, false);
        ztApplication app = ztApplication.getInstance();
        if (null != app) {
            app.setTask(null);
        }
        ztApplication.getInstance().setTaskCount(0);
    }

    private void connectedRouter(String url) {
        LogUtils.e("ws:usl=" + url);
        if (!TextUtils.isEmpty(url) && url.contains("ws")) {
            stopWebSocketConnect();
            wsManager = new WsManager.Builder(getBaseContext()).client(
                    new OkHttpClient().newBuilder()
                            .pingInterval(15, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true)
                            .build())
                    .needReconnect(true)
                    .wsUrl(url)
                    .build();
            wsManager.setWsStatusListener(wsStatusListener);
            wsManager.startConnect();
        } else {
            Toast.makeText(getBaseContext(), "请填写需要链接的地址", Toast.LENGTH_SHORT).show();
        }
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message pMessage) {
            switch (pMessage.what) {
                case 200:
                    heartbeat_zero = GsonUtil.createGsonString(new HeartBeatZero(0));
                    getHeartBeatOne();
                    LogUtils.i("heartbeat_one=" + heartbeat_one);
                    heartbeat_two = GsonUtil.createGsonString(new HeartBeatTwo(2));
                    heartbeat_three = GsonUtil.createGsonString(new HeartBeatThree(CommandService.this, 3));

                    int taskStatus = Preferences.getInt(getBaseContext(), Constant.KEY_TASK_STATUS);
//                    LogUtils.e("当前任务状态 taskStatus=" + taskStatus);
                    if (wsManager.isWsConnected()) {
                        sendMessageToRouter(taskStatus == 0 ? heartbeat_zero
                                : taskStatus == 1 ? heartbeat_one
                                : taskStatus == 2 ? heartbeat_two
                                : heartbeat_three);
                    } else {
                        LogUtils.e("wsUnConnected......websocket未连接.......");
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private String getHeartBeatOne() {
        JSONObject obj = new JSONObject();
        try {
            if (Preferences.getBoolean(getBaseContext(), Constant.KEY_TASK_INIT_NOT_START)) {
                heartbeat_one = obj.put("heartbeat", 1).toString();
                LogUtils.i("heartbeat_one_not_init_start=" + heartbeat_one);
            } else {
                heartbeat_one = obj.put("heartbeat", 1)
                        .put("statistical", Preferences.getInt(CommandService.this, Constant.KEY_TASK_EXECUTE_STATISTICAL))
                        .put("spentTime", Preferences.getInt(CommandService.this, Constant.KEY_TASK_SPENT_TIME))
                        .toString();
            }
        } catch (JSONException pE) {
            pE.printStackTrace();
            throw new RuntimeException("heartbeat_one 解析错误");
        }
        return heartbeat_one;
    }

    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    private void initHeartBeat() {
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(200);
            }
        }, 1, 10, TimeUnit.SECONDS);
    }


    private void stopHeartBeat() {
        if (service != null) {
            service.shutdown();
            service = null;
        }
    }

    private void sendMessageToRouter(String content) {
        if (!TextUtils.isEmpty(content)) {
            LogUtils.e("sendMessageToRouter=" + content);
            if (wsManager != null && wsManager.isWsConnected()) {
                boolean isSend = wsManager.sendMessage(content);
                if (isSend) {
                    LogUtils.i(content + "\n\n");
                } else {
                    LogUtils.e(Spanny.spanText("消息发送失败\n", new ForegroundColorSpan(
                            ContextCompat.getColor(getBaseContext(), android.R.color.holo_red_light))).toString());
                }
            } else {
                LogUtils.e(Spanny.spanText("请先连接服务器\n", new ForegroundColorSpan(
                        ContextCompat.getColor(getBaseContext(), android.R.color.holo_red_light))).toString());
            }
        } else {
            LogUtils.e(Spanny.spanText("请填写需要发送的内容\n", new ForegroundColorSpan(
                    ContextCompat.getColor(getBaseContext(), android.R.color.holo_red_light))).toString());
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiverEventType(MessageEvent event) {
        LogUtils.e(" ThreadMode.MAIN" + event.toString());
        if (null != event) {
            mHandler.sendEmptyMessage(200);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMessageEventPosting(String event) {
        LogUtils.e("event[" + event + "]");
        try {
            JSONObject obj = new JSONObject(event);
            int code = obj.optInt("code");
            String msg = obj.optString("msg");
            JSONArray array = obj.optJSONArray("data");

            if (200 == code) {
                ArrayList<Task> lTasks = Task.parse(array);
                Task task = lTasks.get(0);
                Preferences.set(getBaseContext(), Constant.KEY_TASK_BEAN, task);
                Preferences.set(getBaseContext(), Constant.KEY_TASK_TYPE, task.getType());
                Preferences.set(getBaseContext(), Constant.KEY_TASK_MARKET, task.getAppMarket());

                ztApplication.getInstance().setTask(task);
                Preferences.set(getBaseContext(), Constant.KEY_TASK_INIT_NOT_START, false);
                executeTask(task);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtils.e("Exception TASK_BEAN=", ExceptionEngine.catchException(e).getMsg());
            mHandler.sendEmptyMessage(200);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("Exception=", ExceptionEngine.catchException(e).getMsg());
        }
    }

    /**
     * 到指定市场执行 特定类型任务
     */
    private void executeTask(Task task) {
        if (Preferences.getInt(getBaseContext(), Constant.KEY_TASK_STATUS) == 1) {
            TaskIntentService.startActionLaunchTask(this, task.getAppMarket());
        } else {
            LogUtils.e("其它类型任务状态 不执行任务 TaskStatus = " +
                    Preferences.getInt(getBaseContext(), Constant.KEY_TASK_STATUS));
        }
    }

    private Spanned fromHtmlText(String s) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(s);
        }
        return result;
    }

    public void postedDelayExecute(long second) {
        String cmd = "sleep  " + second + ";";
        ShellUtils.execCommand(cmd, true);
    }

}
