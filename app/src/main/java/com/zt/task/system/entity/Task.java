package com.zt.task.system.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Task implements Parcelable {

    public static ArrayList<Task> parse(JSONArray array) {
        ArrayList<Task> items = new ArrayList<>();
        if (array == null) {
            return items;
        }
        for (int i = 0; i < array.length(); ++i) {
            JSONObject j = array.optJSONObject(i);
            Builder b = Task.newBuilder()
                    .id(j.optInt("id"))
                    .taskid(j.optInt("taskid"))
                    .type(j.optString("type"))
                    .amount(j.optInt("amount"))
                    .statistical(j.optInt("statistical"))
                    .keyWords(j.optString("keyWords"))
                    .appMarket(j.optString("appMarket"))
                    .productName(j.optString("productName"))
                    .productPackage(j.optString("productPackage"));
            items.add(b.build());
        }
        return items;
    }


    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(Task copy) {
        Builder builder = new Builder();
        builder.id = copy.id;
        builder.taskid = copy.taskid;
        builder.type = copy.type;
        builder.amount = copy.amount;
        builder.statistical = copy.statistical;
        builder.keyWords = copy.keyWords;
        builder.appMarket = copy.appMarket;
        builder.productName = copy.productName;
        builder.productPackage = copy.productPackage;
        return builder;
    }

    private Task(Builder builder) {
        setId(builder.id);
        setTaskid(builder.taskid);
        setType(builder.type);
        setAmount(builder.amount);
        setStatistical(builder.statistical);
        setKeyWords(builder.keyWords);
        setAppMarket(builder.appMarket);
        setProductName(builder.productName);
        setProductPackage(builder.productPackage);
    }

    private int id;
    private int taskid;
    private String type;
    private int amount;
    private int statistical;
    private String keyWords;
    private String appMarket;
    private String productName;
    private String productPackage;

    public int getId() {
        return id;
    }

    public void setId(int pId) {
        id = pId;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int pTaskid) {
        taskid = pTaskid;
    }

    public String getType() {
        return type;
    }

    public void setType(String pType) {
        type = pType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int pAmount) {
        amount = pAmount;
    }

    public int getStatistical() {
        return statistical;
    }

    public void setStatistical(int pStatistical) {
        statistical = pStatistical;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String pKeyWords) {
        keyWords = pKeyWords;
    }

    public String getAppMarket() {
        return appMarket;
    }

    public void setAppMarket(String pAppMarket) {
        appMarket = pAppMarket;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String pProductName) {
        productName = pProductName;
    }

    public String getProductPackage() {
        return productPackage;
    }

    public void setProductPackage(String pProductPackage) {
        productPackage = pProductPackage;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.taskid);
        dest.writeString(this.type);
        dest.writeInt(this.amount);
        dest.writeInt(this.statistical);
        dest.writeString(this.keyWords);
        dest.writeString(this.appMarket);
        dest.writeString(this.productName);
        dest.writeString(this.productPackage);
    }

    public Task() {
    }

    protected Task(Parcel in) {
        this.id = in.readInt();
        this.taskid = in.readInt();
        this.type = in.readString();
        this.amount = in.readInt();
        this.statistical = in.readInt();
        this.keyWords = in.readString();
        this.appMarket = in.readString();
        this.productName = in.readString();
        this.productPackage = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public static final class Builder {
        public int id;
        public int taskid;
        public String type;
        public int amount;
        public int statistical;
        public String keyWords;
        public String appMarket;
        public String productName;
        public String productPackage;

        private Builder() {
        }

        public Builder id(int val) {
            id = val;
            return this;
        }

        public Builder taskid(int val) {
            taskid = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder amount(int val) {
            amount = val;
            return this;
        }

        public Builder statistical(int val) {
            statistical = val;
            return this;
        }

        public Builder keyWords(String val) {
            keyWords = val;
            return this;
        }

        public Builder appMarket(String val) {
            appMarket = val;
            return this;
        }

        public Builder productName(String val) {
            productName = val;
            return this;
        }

        public Builder productPackage(String val) {
            productPackage = val;
            return this;
        }

        public Task build() {
            return new Task(this);
        }
    }

}
