package com.example.MyBookShopApp.data.repository;

import com.example.MyBookShopApp.data.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository <Author, Integer> {
}
