package com.example.MyBookShopApp.data.repository;

import com.example.MyBookShopApp.data.entities.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {
}
