package com.zt.task.system.entity;

import java.io.Serializable;

public class HeartBeatZero implements Serializable {
    public HeartBeatZero(int pHeartbeat) {
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
