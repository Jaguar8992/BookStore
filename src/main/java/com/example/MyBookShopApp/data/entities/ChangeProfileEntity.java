package com.example.MyBookShopApp.data.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "change_profile_entity")
public class ChangeProfileEntity {

    @Id
    private String id;
    private String name;
    private String mail;
    private String phone;
    private String password;
    private LocalDateTime expireTime;

    public ChangeProfileEntity(String id, String name, String mail, String phone, String password, Integer expireIn) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.phone = phone;
        this.password = password;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    public ChangeProfileEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isExpired(){
        return LocalDateTime.now().isAfter(expireTime);
    }
}
