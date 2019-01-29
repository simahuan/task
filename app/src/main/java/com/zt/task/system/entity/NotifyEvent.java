package com.zt.task.system.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;

/**
 * @author
 */
public class NotifyEvent implements Parcelable{
    AccessibilityEvent mAccessibilityEvent;

    public AccessibilityEvent getAccessibilityEvent() {
        return mAccessibilityEvent;
    }

    public void setAccessibilityEvent(AccessibilityEvent pAccessibilityEvent) {
        mAccessibilityEvent = pAccessibilityEvent;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mAccessibilityEvent, flags);
    }

    public NotifyEvent() {
    }

    protected NotifyEvent(Parcel in) {
        this.mAccessibilityEvent = in.readParcelable(AccessibilityEvent.class.getClassLoader());
    }

    public static final Creator<NotifyEvent> CREATOR = new Creator<NotifyEvent>() {
        @Override
        public NotifyEvent createFromParcel(Parcel source) {
            return new NotifyEvent(source);
        }

        @Override
        public NotifyEvent[] newArray(int size) {
            return new NotifyEvent[size];
        }
    };
}
