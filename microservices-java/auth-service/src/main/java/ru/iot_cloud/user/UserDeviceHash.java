package ru.iot_cloud.user;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

class UserDeviceHash extends PanacheEntity {
    public Long userId;
    public String deviceId;
    public String hash;
}
