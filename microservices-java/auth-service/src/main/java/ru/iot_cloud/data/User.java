package ru.iot_cloud.data;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "iotcloud_users")
public class User extends PanacheEntity {
    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String lastName;

    @Column(nullable = false, unique = true)
    public String email;

    @Column(nullable = false)
    public boolean isActive;

    @Column(nullable = false)
    public LocalDateTime registrationDate;

    @Column
    public LocalDateTime lastLoginDate;

    @Column
    public String confirmationCode;

    public User() {
        this.isActive = true;
        this.registrationDate = LocalDateTime.now();
    }

    public void updateLastLoginDate() {
        this.lastLoginDate = LocalDateTime.now();
    }

    public void setConfirmationCode(String code) {
        this.confirmationCode = code;
    }
}
