package com.example.MyBookShopApp.data.repository;

import com.example.MyBookShopApp.data.entities.SmsCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsCodeRepository extends JpaRepository<SmsCode,Long> {

    public SmsCode findByCode(String code);
}
