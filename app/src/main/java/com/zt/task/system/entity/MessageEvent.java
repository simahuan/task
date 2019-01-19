package com.zt.task.system.entity;

public class MessageEvent {
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String pEventName) {
        eventName = pEventName;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int pTaskStatus) {
        taskStatus = pTaskStatus;
    }

    public MessageEvent(String pEventName, int pTaskStatus) {
        eventName = pEventName;
        taskStatus = pTaskStatus;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "eventName='" + eventName + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }

    private String eventName;
    private int taskStatus;
}
