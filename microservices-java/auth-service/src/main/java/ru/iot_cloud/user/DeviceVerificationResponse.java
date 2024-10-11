package ru.iot_cloud.user;

class DeviceVerificationResponse {
    public String newDeviceHash;

    public DeviceVerificationResponse(String newDeviceHash) {
        this.newDeviceHash = newDeviceHash;
    }
}