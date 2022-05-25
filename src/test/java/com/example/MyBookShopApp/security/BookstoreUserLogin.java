package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.security.jwt.JWTUtil;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
public class BookstoreUserLogin {

    private final BookstoreUserRegister userRegister;
    private final JWTUtil jwtUtil;

    @Autowired
    public BookstoreUserLogin(BookstoreUserRegister userRegister, JWTUtil jwtUtil) {
        this.userRegister = userRegister;
        this.jwtUtil = jwtUtil;
    }

    @Test
    void login(){
        String email = "Jaguar8992@ya.ru";
        String phone = "+7 (111) 111-11-11";
        String code = "1234567";

        ContactConfirmationPayload payload = new ContactConfirmationPayload();
        payload.setCode(code);
        payload.setContact(email);

        ContactConfirmationResponse response = userRegister.jwtLogin(payload);
        assertNotNull(response);
        String result = jwtUtil.extractUsername(response.getResult());
        assertTrue(CoreMatchers.is(result).matches(email));

        payload.setContact(phone);
        response = userRegister.login(payload);
        assertNotNull(response);

        assertThrows(Exception.class, () -> {
            payload.setContact("random");
            userRegister.login(payload);
        });
    }
}
