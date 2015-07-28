package com.supergenieapp.android.Objects;

/**
 * Created by Raviteja on 6/15/2015.
 */
public class Order implements Comparable<Order> {
    private String payment_url;

    private String id;

    private String title;

    private String updated_at;

    private String service_provider;

    private String description;

    private String created_at;

    private String category_id;

    private String user_id;

    private String agent_id;

    private String cost;

    private String last_message_id;

    @Override
    public int compareTo(Order obj) {
        return String.valueOf(this.id).compareTo(obj.id);
    }

    public Order(String id, String payment_url, String title, String updated_at, String service_provider, String description, String created_at, String category_id, String user_id, String agent_id, String cost, String last_message_id) {
        this.payment_url = payment_url;
        this.id = id;
        this.title = title;
        this.updated_at = updated_at;
        this.service_provider = service_provider;
        this.description = description;
        this.created_at = created_at;
        this.category_id = category_id;
        this.user_id = user_id;
        this.agent_id = agent_id;
        this.cost = cost;
        this.last_message_id = last_message_id;
    }

    public String getPayment_url() {
        return payment_url;
    }

    public void setPayment_url(String payment_url) {
        this.payment_url = payment_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getService_provider() {
        return service_provider;
    }

    public void setService_provider(String service_provider) {
        this.service_provider = service_provider;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getLast_message_id() {
        return last_message_id;
    }

    public void setLast_message_id(String last_message_id) {
        this.last_message_id = last_message_id;
    }
}

