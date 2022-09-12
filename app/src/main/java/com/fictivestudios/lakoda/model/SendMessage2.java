package com.fictivestudios.lakoda.model;

public class SendMessage2 {
    String message, type,thumbnail;
    Integer sender_id, receiver_id;


    public SendMessage2( Integer sender_id, Integer receiver_id,String message, String type) {
        this.message = message;
        this.type = type;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
    }

    public SendMessage2(Integer sender_id, Integer receiver_id,String message, String type, String thumbnail) {
        this.message = message;
        this.type = type;
        this.thumbnail = thumbnail;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Integer getSender_id() {
        return sender_id;
    }

    public void setSender_id(Integer sender_id) {
        this.sender_id = sender_id;
    }

    public Integer getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(Integer receiver_id) {
        this.receiver_id = receiver_id;
    }
}

