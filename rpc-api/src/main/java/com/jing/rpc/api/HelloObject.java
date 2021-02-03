package com.jing.rpc.api;

import java.io.Serializable;

public class HelloObject implements Serializable {

    private int id;
    private String message;

    public HelloObject() {
    }

    public HelloObject(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
