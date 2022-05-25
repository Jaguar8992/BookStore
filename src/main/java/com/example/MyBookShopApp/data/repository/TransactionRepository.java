package com.example.MyBookShopApp.data.repository;

import com.example.MyBookShopApp.data.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository <Transaction, String> {
}
