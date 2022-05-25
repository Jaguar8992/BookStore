package com.example.MyBookShopApp.errs;

public class BookstoreUserExistException extends Exception{
    public BookstoreUserExistException(String message) {
        super(message);
    }
}
