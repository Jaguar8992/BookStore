package com.example.MyBookShopApp.data.repository;

import com.example.MyBookShopApp.data.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
