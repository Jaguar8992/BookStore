package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.api.GradeRequest;
import com.example.MyBookShopApp.data.services.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rateBook")
public class GradeController {

    private final BookService bookService;

    public GradeController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity <String> rateBook (@RequestBody GradeRequest request){
       return bookService.rateBook(request);
    }
}
