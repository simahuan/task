package com.zt.task.system.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class VpnProfile implements Parcelable {
    private String userName;
    private String password;
    private String session;
    private String address;

//    public static ArrayList<Address> parse(JSONArray array) {
//        ArrayList<Address> items = new ArrayList<>();
//        if (array == null) return items;
//        for (int i = 0; i < array.length(); ++i) {
//            JSONObject j = array.optJSONObject(i);
//            Builder b = Address.newBuilder()
//                    .id(j.optString("AddressId"))
//                    .isDefault(j.optInt("IsDefault") == 1)
//                    .name(j.optString("Name"))
//                    .phone(j.optString("Phone"))
//                    .areaCode(j.optString("AreaCode"))
//                    .street(j.optString("Street"))
//                    .areaFullName(AreaManager.getFullName(j.optString("AreaCode")));
//            items.add(b.build());
//        }
//        return items;
//    }

    private VpnProfile() {
    }

    private VpnProfile(Builder builder) {
        setUserName(builder.userName);
        setPassword(builder.password);
        setSession(builder.session);
        setAddress(builder.address);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(VpnProfile copy) {
        Builder builder = new Builder();
        builder.userName = copy.userName;
        builder.password = copy.password;
        builder.session = copy.session;
        builder.address = copy.address;
        return builder;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String pUserName) {
        userName = pUserName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pPassword) {
        password = pPassword;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String pSession) {
        session = pSession;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String pAddress) {
        address = pAddress;
    }

    public static final class Builder {
        private String userName;
        private String password;
        private String session;
        private String address;

        private Builder() {
        }

        public Builder userName(String val) {
            userName = val;
            return this;
        }

        public Builder password(String val) {
            password = val;
            return this;
        }

        public Builder session(String val) {
            session = val;
            return this;
        }

        public Builder address(String val) {
            address = val;
            return this;
        }

        public VpnProfile build() {
            return new VpnProfile(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.password);
        dest.writeString(this.session);
        dest.writeString(this.address);
    }


    protected VpnProfile(Parcel in) {
        this.userName = in.readString();
        this.password = in.readString();
        this.session = in.readString();
        this.address = in.readString();
    }

    public static final Creator<VpnProfile> CREATOR = new Creator<VpnProfile>() {
        @Override
        public VpnProfile createFromParcel(Parcel source) {
            return new VpnProfile(source);
        }

        @Override
        public VpnProfile[] newArray(int size) {
            return new VpnProfile[size];
        }
    };
}