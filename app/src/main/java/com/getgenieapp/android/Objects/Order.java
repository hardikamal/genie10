package com.getgenieapp.android.Objects;

/**
 * Created by Raviteja on 6/15/2015.
 */
public class Order {
    private int id;

    private String title;

    private OrderCategory category;

    private String service_provider;

    private String description;

    private String created_at;

    private int user_id;

    private int agent_id;

    private double cost;

    private long last_message_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public OrderCategory getCategory() {
        return category;
    }

    public void setCategory(OrderCategory category) {
        this.category = category;
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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(int agent_id) {
        this.agent_id = agent_id;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public long getLast_message_id() {
        return last_message_id;
    }

    public void setLast_message_id(long last_message_id) {
        this.last_message_id = last_message_id;
    }

    public Order(int id, String title, OrderCategory category, String service_provider, String description, String created_at, int user_id, int agent_id, double cost, long last_message_id) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.service_provider = service_provider;
        this.description = description;
        this.created_at = created_at;
        this.user_id = user_id;
        this.agent_id = agent_id;
        this.cost = cost;
        this.last_message_id = last_message_id;
    }
}
