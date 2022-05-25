package com.example.MyBookShopApp.data.repository;


import com.example.MyBookShopApp.data.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
