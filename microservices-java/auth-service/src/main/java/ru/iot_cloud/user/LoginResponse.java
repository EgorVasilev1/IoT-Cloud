package ru.iot_cloud.user;

class LoginResponse {
    public String token;
    public String deviceHash;

    public LoginResponse(String token, String deviceHash) {
        this.token = token;
        this.deviceHash = deviceHash;
    }
}