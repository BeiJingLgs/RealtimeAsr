package com.hanvon.speech.realtime.bean;

public class MobMessage {

    /**
     * httpStatus : 400
     * description : 每分钟发送次数超限
     * detail : 每分钟发送短信的数量超过限制。
     * error : Sending msg over 2 restrictions per minute
     * status : 462
     */

    private int httpStatus;
    private String description;
    private String detail;
    private String error;
    private int status;

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
