package com.magenta.mc.client.android.record;

import java.io.Serializable;

public class ResultRecord implements Serializable {

    protected Boolean error;
    protected Integer errorCode;
    protected String errorMessage;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String toString() {
        return "ResultRecord{" +
                "error=" + error +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}