package com.magenta.mc.client.android.http.record;

import java.io.Serializable;

public class LoginRecord implements Serializable {

    private Boolean md5Password;
    private String username;
    private String password;
    private String accountTechName;

    public Boolean getMd5Password() {
        return md5Password;
    }

    public void setMd5Password(Boolean md5Password) {
        this.md5Password = md5Password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountTechName() {
        return accountTechName;
    }

    public void setAccountTechName(String accountTechName) {
        this.accountTechName = accountTechName;
    }

    public String toString() {
        return "LoginRecord{" +
                "md5Password=" + md5Password +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", accountTechName='" + accountTechName + '\'' +
                '}';
    }
}