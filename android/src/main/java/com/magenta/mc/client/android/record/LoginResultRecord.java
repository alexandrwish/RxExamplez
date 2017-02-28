package com.magenta.mc.client.android.record;

public class LoginResultRecord extends ResultRecord {

    private String id;
    private String name;
    private String login;
    private String token;
    private String globalId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public String toString() {
        return "LoginResultRecord{" +
                "error=" + error +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", login='" + login + '\'' +
                ", token='" + token + '\'' +
                ", globalId='" + globalId + '\'' +
                '}';
    }
}