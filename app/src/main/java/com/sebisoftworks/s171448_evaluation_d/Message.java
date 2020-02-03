package com.sebisoftworks.s171448_evaluation_d;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

class Message {
    private String sender;
    private Date date;
    private String text;


    Message(String sender, Date date, String text) {
        this.sender = sender;
        this.date = date;
        this.text = text;
    }

    String getSender() {
        return sender;
    }

    Date getDate() {
        return date;
    }

    String getText() {
        return text;
    }

    private String toJsonString() {
        return "{\"sender\":\"" + sender + "\",\"text\":\"" + text + "\",\"timestamp\":\"" + date.getTime() + "\"}";
    }

    JSONObject toJSONObject() {
        try {
            return new JSONObject(toJsonString());
        } catch (JSONException e) {
            return null;
        }
    }
}
