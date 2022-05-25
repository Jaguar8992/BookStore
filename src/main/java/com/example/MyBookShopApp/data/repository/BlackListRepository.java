package com.example.MyBookShopApp.data.repository;


import com.example.MyBookShopApp.data.entities.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListRepository extends JpaRepository<BlackList, String> {
}
