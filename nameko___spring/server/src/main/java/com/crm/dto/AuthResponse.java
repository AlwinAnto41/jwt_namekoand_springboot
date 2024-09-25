package com.crm.dto;

public class AuthResponse {
    private Integer status;
    private String message;
    private String error;
    private String access_token;
    private String refresh_token;
    

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", error='" + error + '\'' +
                ", access_token='" + (access_token != null ? "PRESENT" : "null") + '\'' +
                ", refresh_token='" + (refresh_token != null ? "PRESENT" : "null") + '\'' +
                '}';
    }

    public AuthResponse() {
    }

    // Constructor for error responses
    public AuthResponse(Integer status, String error) {
        this.status = status;
        this.error = error;
    }
}

