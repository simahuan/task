package com.zt.task.system.acvitity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zt.task.system.APP;
import com.zt.task.system.R;
import com.zt.task.system.entity.Command;
import com.zt.task.system.entity.HeartBeatOne;
import com.zt.task.system.entity.HeartBeatThree;
import com.zt.task.system.entity.HeartBeatTwo;
import com.zt.task.system.entity.HeartBeatZero;
import com.zt.task.system.entity.Task;
import com.zt.task.system.okhttp.MyDataCallBack;
import com.zt.task.system.okhttp.OkHTTPManger;
import com.zt.task.system.service.MyIntentService;
import com.zt.task.system.util.Constant;
import com.zt.task.system.util.DeviceInfoUtils;
import com.zt.task.system.util.GsonUtil;
import com.zt.task.system.util.LogUtils;
import com.zt.task.system.util.Preferences;
import com.zt.task.system.util.Spanny;
import com.zt.task.system.websocket.WsManager;
import com.zt.task.system.websocket.WsStatusListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import androidx.core.content.ContextCompat;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.ByteString;

public class ThirdActivity extends BaseActivity {
    private final static String TAG = "MainActivity";
    private WsManager wsManager;
    private TextView btn_send, btn_clear, tv_content;
    private EditText edit_url, edit_content;
    String ws = null;

    String heartbeat_zero;
    String heartbeat_one;
    String heartbeat_two;
    String heartbeat_three;

    private String formatWebSocket() {
        String deviceid = DeviceInfoUtils.getIMEI(getBaseContext());
        return String.format("ws://192.168.1.191:2345/?uid=%1$s", deviceid);
    }

    private void getTaskInfos(String url, String token) {
        OkHTTPManger.getInstance().getAsynBackStringWithoutParms(url, token, new MyDataCallBack() {
            @Override
            public void onBefore(Request request) {
                Log.d(TAG, "OkHTTPManger-----onBefore");
            }

            @Override
            public void requestSuccess(Object result) {
                Log.d(TAG, "OkHTTPManger-----requestSuccess");
                EventBus.getDefault().post(result);
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Log.d(TAG, "OkHTTPManger-----requestFailure");
                EventBus.getDefault().post(e.toString());
            }

            @Override
            public void onAfter() {
                Log.d(TAG, "OkHTTPManger-----onAfter");
            }
        });
    }


    private WsStatusListener wsStatusListener = new WsStatusListener() {
        @Override
        public void onOpen(Response response) {
            super.onOpen(response);
            LogUtils.d(TAG, "wsStatusListener-----onOpen");
            tv_content.append(Spanny.spanText("服务器连接成功\n\n", new ForegroundColorSpan(
                    ContextCompat.getColor(getBaseContext(), R.color.colorPrimary))));
        }

        /**
         * @param text
         */
        @Override
        public void onMessage(String text) {
            LogUtils.d(TAG, "wsStatusListener-----onMessage");
            tv_content.append(Spanny.spanText("服务器 " + DateUtils.formatDateTime(getBaseContext(), System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME) + "\n", new ForegroundColorSpan(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary))));
            tv_content.append(fromHtmlText(text) + "\n\n");

            LogUtils.e(Spanny.spanText("服务器 " + DateUtils.formatDateTime(getBaseContext(), System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME) + "\n", new ForegroundColorSpan(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary))).toString());
            LogUtils.d(fromHtmlText(text) + "\n\n");


            if (!TextUtils.isEmpty(text)) {
                try {
                    JSONObject object = new JSONObject(text);
                    Command cmd = Command.parse(object);
                    Preferences.set(getBaseContext(), Constant.KEY_COMMAND_BEAN, cmd);

                    if (!TextUtils.isEmpty(cmd.getTokens())
                            && !TextUtils.isEmpty(cmd.getUrl())
                            && cmd.getStatus() == 1
                            ) {
                        Preferences.set(getBaseContext(), Constant.KEY_TASK_STATUS, Constant.TASK_EXECUTE);
                        getTaskInfos(cmd.getUrl(), cmd.getTokens());
                        Preferences.set(getBaseContext(), Constant.KEY_TASK_CREATE_TIME, System.currentTimeMillis());

                    } else if (!TextUtils.isEmpty(cmd.getTokens())
                            && !TextUtils.isEmpty(cmd.getUrl())
                            && cmd.getStatus() == 2) {
                        Preferences.set(getBaseContext(), Constant.KEY_TASK_STATUS, Constant.TASK_CANCEL);
                    }
                } catch (Exception pE) {
                    pE.printStackTrace();
                }
            }
        }

        @Override
        public void onMessage(ByteString bytes) {
            super.onMessage(bytes);
            Log.d(TAG, "wsStatusListener-----onMessage");
        }

        @Override
        public void onReconnect() {
            super.onReconnect();
//            clearReportZero();
            LogUtils.d("wsStatusListener----onReconnect");
            tv_content.append(Spanny.spanText("服务器重连接中...\n", new ForegroundColorSpan(
                    ContextCompat.getColor(getBaseContext(), android.R.color.holo_red_light))));
        }

        @Override
        public void onClosing(int code, String reason) {
            super.onClosing(code, reason);
            Log.d(TAG, "wsStatusListener-----onClosing");
            tv_content.append(Spanny.spanText("服务器连接关闭中...\n", new ForegroundColorSpan(
                    ContextCompat.getColor(getBaseContext(), android.R.color.holo_red_light))));
        }

        @Override
        public void onClosed(int code, String reason) {
            super.onClosed(code, reason);
            Log.d(TAG, "wsStatusListener-----onClosed");
            tv_content.append(Spanny.spanText("服务器连接已关闭\n", new ForegroundColorSpan(ContextCompat.getColor(getBaseContext(), android.R.color.holo_red_light))));
        }

        @Override
        public void onFailure(Throwable t, Response response) {
            super.onFailure(t, response);
            LogUtils.e("wsStatusListener-----onFailure");
            clearReportZero();

            tv_content.append(Spanny.spanText("服务器连接失败\n", new ForegroundColorSpan(
                    ContextCompat.getColor(getBaseContext(), android.R.color.holo_red_light))));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        EventBus.getDefault().register(this);
        btn_send = (TextView) findViewById(R.id.btn_send);
        btn_clear = (TextView) findViewById(R.id.btn_clear);
        tv_content = (TextView) findViewById(R.id.tv_content);
        edit_url = (EditText) findViewById(R.id.edit_url);
        edit_content = (EditText) findViewById(R.id.edit_content);
        String imei = DeviceInfoUtils.getIMEI(getBaseContext());
        edit_content.setText(imei);

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_content.setText("");
            }
        });
        clearReportZero();
        ws = formatWebSocket();
        connectedRouter(ws);
        startHeartBeat();
    }

    /**
     * 清空上报数据
     */
    private void clearReportZero() {
        LogUtils.e("清空上报数据....");
        Preferences.set(this, Constant.KEY_TASK_SPENT_TIME, 0);
        Preferences.set(this, Constant.KEY_TASK_EXECUTE_STATISTICAL, 0);
        Preferences.set(ThirdActivity.this, Constant.KEY_TASK_STATUS, Constant.TASK_IDLE);
        APP.getInstance().setTaskCount(0);
    }

    private void connectedRouter(String url) {
        if (!TextUtils.isEmpty(url) && url.contains("ws")) {
            if (wsManager != null) {
                wsManager.stopConnect();
                wsManager = null;
            }
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


    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message pMessage) {
            switch (pMessage.what) {
                case 200:
                    heartbeat_zero = GsonUtil.createGsonString(new HeartBeatZero(0));
                    heartbeat_one = GsonUtil.createGsonString(new HeartBeatOne(ThirdActivity.this, 1));
                    LogUtils.e("heartbeat_one=" + heartbeat_one);
                    heartbeat_two = GsonUtil.createGsonString(new HeartBeatTwo(2));
                    heartbeat_three = GsonUtil.createGsonString(new HeartBeatThree(ThirdActivity.this, 3));

                    int taskStatus = Preferences.getInt(getBaseContext(), Constant.KEY_TASK_STATUS);
                    LogUtils.e("taskStatus=" + taskStatus);
                    sendMessageToRouter(taskStatus == 0 ? heartbeat_zero : taskStatus == 1 ? heartbeat_one : taskStatus == 2 ? heartbeat_two : heartbeat_three);
                    break;
            }
            return false;
        }
    });

    // 心跳机制
    Timer mTimer = new Timer(true);
    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(200);
        }
    };

    private void startHeartBeat() {
        mTimer.schedule(mTimerTask, 1000, 10 * 1000);
    }

    private void stopHeartBeat() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        stopHeartBeat();
    }

    private void sendMessageToRouter(String content) {
        if (!TextUtils.isEmpty(content)) {
            if (wsManager != null && wsManager.isWsConnected()) {
                boolean isSend = wsManager.sendMessage(content);
                if (isSend) {
                    tv_content.append(Spanny.spanText(
                            "我 " + DateUtils.formatDateTime(getBaseContext(), System.currentTimeMillis(),
                                    DateUtils.FORMAT_SHOW_TIME) + "\n", new ForegroundColorSpan(
                                    ContextCompat.getColor(getBaseContext(), android.R.color.holo_green_light))));
                    tv_content.append(content + "\n\n");
                } else {
                    tv_content.append(Spanny.spanText("消息发送失败\n", new ForegroundColorSpan(
                            ContextCompat.getColor(getBaseContext(), android.R.color.holo_red_light))));
                }
                showOrHideInputMethod();
                edit_content.setText("");
            } else {
                tv_content.append(Spanny.spanText("请先连接服务器\n", new ForegroundColorSpan(
                        ContextCompat.getColor(getBaseContext(), android.R.color.holo_red_light))));
            }
        } else {
            tv_content.append(Spanny.spanText("请填写需要发送的内容\n", new ForegroundColorSpan(
                    ContextCompat.getColor(getBaseContext(), android.R.color.holo_red_light))));
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMessageEventPosting(String event) {
        Log.e(TAG, "event[" + event + "]");
        try {
            JSONObject obj = new JSONObject(event);
            int code = obj.optInt("code");
            String msg = obj.optString("msg");
            JSONArray array = obj.optJSONArray("data");

            if (200 == code) {
                ArrayList<Task> lTasks = Task.parse(array);
                Task task = lTasks.get(0);
                Preferences.set(getBaseContext(), Constant.KEY_TASK_BEAN, task);
                APP.getInstance().setTask(task);
                executeTask();
            }
        } catch (JSONException pE) {
            pE.printStackTrace();
            LogUtils.e("发生异常", pE.getMessage());
        }
    }

    private void executeTask() {
        if (Preferences.getInt(getBaseContext(), Constant.KEY_TASK_STATUS) == 1) {
            MyIntentService.startActionTask(this, 1, 0);
        } else {
            LogUtils.e("其它类型任务状态 不执行任务 TaskStatus = ", Preferences.getInt(getBaseContext(), Constant.KEY_TASK_STATUS));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (wsManager != null) {
            wsManager.stopConnect();
            wsManager = null;
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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

    private void showOrHideInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
