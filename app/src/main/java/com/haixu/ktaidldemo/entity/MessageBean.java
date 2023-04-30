package com.haixu.ktaidldemo.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageBean implements Parcelable {

    private String content;

    private boolean isSendMessageSuccess;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSendMessageSuccess() {
        return isSendMessageSuccess;
    }

    public void setSendMessageSuccess(boolean sendMessageSuccess) {
        isSendMessageSuccess = sendMessageSuccess;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeByte(this.isSendMessageSuccess ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.content = source.readString();
        this.isSendMessageSuccess = source.readByte() != 0;
    }

    public MessageBean() {
    }

    protected MessageBean(Parcel in) {
        this.content = in.readString();
        this.isSendMessageSuccess = in.readByte() != 0;
    }

    public static final Parcelable.Creator<MessageBean> CREATOR = new Parcelable.Creator<MessageBean>() {
        @Override
        public MessageBean createFromParcel(Parcel source) {
            return new MessageBean(source);
        }

        @Override
        public MessageBean[] newArray(int size) {
            return new MessageBean[size];
        }
    };
}
