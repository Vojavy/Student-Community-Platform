package com.vojavy.AlmAgoraHub.responses;

public class LoginResponse {
    private String token;
    private long expires;
    private String type;

    public LoginResponse(String token, long expires, String type) {
        this.token = token;
        this.expires = expires;
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
