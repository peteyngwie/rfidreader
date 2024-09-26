package com.smartcity.api;

public class ApiResponse<T> {
    private int status;
    private String message;
    private T result;

    public ApiResponse(int status, String message, T result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }

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

    public T getResult() {
        return result;
    }   // json string 的主體

    public void setResult(T result) {
        this.result = result;
    }

    public ApiResponse(int status, T result) {
        this.result = result;
        this.status = 0;
    }

    public static ApiResponse<Object> ok(Object result) {
        return new ApiResponse<>(0, result);
    }

    public static ApiResponse<Object> error(String messgae, Object result) {
        return new ApiResponse<>(1, messgae, result);
    }

    public boolean isOk() {
        return (this.status == 0);
    }

}
