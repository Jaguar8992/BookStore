package com.example.MyBookShopApp.data.repository;

import com.example.MyBookShopApp.data.entities.PurchaseBookKey;
import com.example.MyBookShopApp.data.entities.PurchasedBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchasedBookRepository extends JpaRepository <PurchasedBook, PurchaseBookKey> {
}
