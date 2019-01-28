package com.zt.task.system.util;

import android.os.Parcel;
import android.os.Parcelable;

public final class ParcelableUtil {
    private ParcelableUtil() {
    }

    public static byte[] marshal(Parcelable parcelable) {
        if (parcelable == null) {
            return null;
        }
        Parcel parcel = Parcel.obtain();
        parcelable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    public static Parcel unmarshal(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        // This is extremely important!
        parcel.setDataPosition(0);
        return parcel;
    }

    public static <T> T unmarshal(byte[] bytes, Parcelable.Creator<T> creator) {
        Parcel parcel = unmarshal(bytes);
        T result = creator.createFromParcel(parcel);
        parcel.recycle();
        return result;
    }
}
