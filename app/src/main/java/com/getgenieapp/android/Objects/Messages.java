package com.getgenieapp.android.Objects;

/**
 * Created by Raviteja on 6/16/2015.
 */
public class Messages implements Comparable<Messages> {

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof Messages))
            return false;
        Messages u = (Messages) obj;

        return this.getCreatedAt() == u.getCreatedAt() && this.get_id().equals(u.get_id());
    }

    @Override
    public int compareTo(Messages obj) {
        return Long.valueOf(this.createdAt).compareTo(obj.createdAt);
    }

    private String _id;
    private int messageType;
    private int category;
    private MessageValues messageValues;
    private int status;
    private long createdAt;
    private long updatedAt;
    private int direction;

    public Messages(String _id, int messageType, int category, MessageValues messageValues, int status, long createdAt, long updatedAt, int direction) {
        this._id = _id;
        this.messageType = messageType;
        this.category = category;
        this.messageValues = messageValues;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.direction = direction;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public MessageValues getMessageValues() {
        return messageValues;
    }

    public void setMessageValues(MessageValues messageValues) {
        this.messageValues = messageValues;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
