package com.zt.task.system.entity;

import java.io.Serializable;

public class HeartBeatTwo implements Serializable {
    public HeartBeatTwo(int pHeartbeat) {
        heartbeat = pHeartbeat;
    }

    public int getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(int pHeartbeat) {
        heartbeat = pHeartbeat;
    }

    public int heartbeat;
}
