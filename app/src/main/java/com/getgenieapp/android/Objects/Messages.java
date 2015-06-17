package com.getgenieapp.android.Objects;

/**
 * Created by Raviteja on 6/16/2015.
 */
public class Messages {
    private String _id;
    private int agent_id;
    private int sender_id;
    private String category;
    private String category_type;
    private String[] category_keys;
    private MessageValues category_value;
    private MessageStatus status;
    private long created_at;
    private long updated_at;
    private int direction;

    public Messages(String _id, int agent_id, int sender_id, String category, String category_type, String[] category_keys, MessageValues category_value, MessageStatus status, long created_at, long updated_at, int direction) {
        this._id = _id;
        this.agent_id = agent_id;
        this.sender_id = sender_id;
        this.category = category;
        this.category_type = category_type;
        this.category_keys = category_keys;
        this.category_value = category_value;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(int agent_id) {
        this.agent_id = agent_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory_type() {
        return category_type;
    }

    public void setCategory_type(String category_type) {
        this.category_type = category_type;
    }

    public String[] getCategory_keys() {
        return category_keys;
    }

    public void setCategory_keys(String[] category_keys) {
        this.category_keys = category_keys;
    }

    public MessageValues getCategory_value() {
        return category_value;
    }

    public void setCategory_value(MessageValues category_value) {
        this.category_value = category_value;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }
}
