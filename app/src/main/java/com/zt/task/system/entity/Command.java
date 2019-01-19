package com.zt.task.system.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Command implements Parcelable {
    private String tokens;
    /**
     *
     */
    private int taskid;
    /**
     * 任务编号
     */
    private int id;
    private long ts;
    private String url;
    private int status;


    public static Command parse(JSONObject j) {
        if (j == null) return null;
        Builder b = Command.newBuilder()
                .id(j.optInt("id"))
                .taskid(j.optInt("taskid"))
                .tokens(j.optString("tokens"))
                .ts(j.optLong("ts"))
                .url(j.optString("url"))
                .status(j.optInt("status"));
        return b.build();
    }

    private Command(Builder builder) {
        id = builder.id;
        taskid = builder.taskid;
        tokens = builder.tokens;
        ts = builder.ts;
        url = builder.url;
        status = builder.status;
    }

    Command(JSONObject o) {
        id = o.optInt("id");
        taskid = o.optInt("taskid");
        tokens = o.optString("tokens");
        ts = o.optLong("ts");
        url = o.optString("url");
        status = o.optInt("status");
    }

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String pTokens) {
        tokens = pTokens;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int pTaskid) {
        taskid = pTaskid;
    }

    public int getId() {
        return id;
    }

    public void setId(int pId) {
        id = pId;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long pTs) {
        ts = pTs;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String pUrl) {
        url = pUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int pStatus) {
        status = pStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tokens);
        dest.writeInt(this.taskid);
        dest.writeInt(this.id);
        dest.writeLong(this.ts);
        dest.writeString(this.url);
        dest.writeInt(this.status);
    }

    public Command() {
    }

    protected Command(Parcel in) {
        this.tokens = in.readString();
        this.taskid = in.readInt();
        this.id = in.readInt();
        this.ts = in.readLong();
        this.url = in.readString();
        this.status = in.readInt();
    }

    public static final Creator<Command> CREATOR = new Creator<Command>() {
        @Override
        public Command createFromParcel(Parcel source) {
            return new Command(source);
        }

        @Override
        public Command[] newArray(int size) {
            return new Command[size];
        }
    };

    public static Command.Builder newBuilder() {
        return new Command.Builder();
    }

    public static final class Builder {
        private int id;
        private int taskid;
        private String tokens;
        private long ts;
        private String url;
        private int status;

        private Builder() {

        }

        public Command.Builder id(int val) {
            id = val;
            return this;
        }

        public Command.Builder taskid(int val) {
            taskid = val;
            return this;
        }

        public Command.Builder tokens(String val) {
            tokens = val;
            return this;
        }

        public Command.Builder ts(long val) {
            ts = val;
            return this;
        }

        public Command.Builder url(String val) {
            url = val;
            return this;
        }

        public Command.Builder status(int val) {
            status = val;
            return this;
        }

        public Command build() {
            return new Command(this);
        }
    }
}
