package com.smartcity.api;

public class Response {

    private int status;
    private String message;
    private InterBoxCgs result;
    private Sip result1 ;
    private boolean ok;
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public InterBoxCgs getResult() {
        return result;
    }
    public Sip getResult1() {
        return result1 ;
    }
    public void setResult(InterBoxCgs result) {
        this.result = result;
    }
    public boolean isOk() {
        return ok;
    }
    public void setOk(boolean ok) {
        this.ok = ok;
    }

}