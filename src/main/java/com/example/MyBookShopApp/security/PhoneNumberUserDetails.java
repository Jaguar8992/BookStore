package com.example.MyBookShopApp.security;

import org.springframework.security.core.userdetails.UserDetails;

public class PhoneNumberUserDetails extends BookstoreUserDetails {


    public PhoneNumberUserDetails(BookstoreUser bookstoreUser) {
        super(bookstoreUser);
    }

    @Override
    public String getUsername() {
        return getBookstoreUser().getPhone();
    }
}
