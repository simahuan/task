package com.zt.task.system.entity;

import android.content.Context;

import com.zt.task.system.util.Constant;
import com.zt.task.system.util.Preferences;

import java.io.Serializable;

public class HeartBeatOne implements Serializable {

    public int getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(int pHeartbeat) {
        heartbeat = pHeartbeat;
    }

    public int getStatistical() {
        return statistical;
    }

    public void setStatistical(int pStatistical) {
        statistical = pStatistical;
    }

    public int getSpentTime() {
        return spentTime;
    }

    public void setSpentTime(int pSpentTime) {
        spentTime = pSpentTime;
    }

    public HeartBeatOne(Context pContext, int pHeartbeat) {
        heartbeat = pHeartbeat;
        statistical = Preferences.getInt(pContext, Constant.KEY_TASK_EXECUTE_STATISTICAL);
        spentTime = Preferences.getInt(pContext, Constant.KEY_TASK_SPENT_TIME);
    }

    public int heartbeat;
    public int statistical;
    public int spentTime;
}
