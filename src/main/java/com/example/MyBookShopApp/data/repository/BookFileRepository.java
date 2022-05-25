package com.example.MyBookShopApp.data.repository;

import com.example.MyBookShopApp.data.entities.BookFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookFileRepository extends JpaRepository<BookFile, Integer> {

    public BookFile findBookFileByHash(String hash);
}
