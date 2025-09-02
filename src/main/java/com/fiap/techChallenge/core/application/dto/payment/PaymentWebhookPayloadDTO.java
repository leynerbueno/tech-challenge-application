package com.fiap.techChallenge.core.application.dto.payment;

public class PaymentWebhookPayloadDTO {
    private String action;
    private String api_version;
    private Data data;
    private String date_created;
    private String id;
    private boolean live_mode;
    private String type;
    private long user_id;


    public static class Data {
        private String id;
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getApi_version() {
        return api_version;
    }

    public void setApi_version(String api_version) {
        this.api_version = api_version;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLive_mode() {
        return live_mode;
    }

    public void setLive_mode(boolean live_mode) {
        this.live_mode = live_mode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }
}
