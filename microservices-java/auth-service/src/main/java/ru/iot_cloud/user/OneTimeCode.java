package ru.iot_cloud.user;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import java.time.LocalDateTime;

class OneTimeCode extends PanacheEntity {
    public Long userId;
    public String code;
    public LocalDateTime expirationTime;
}