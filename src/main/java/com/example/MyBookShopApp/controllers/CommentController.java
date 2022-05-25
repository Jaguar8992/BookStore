package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.api.CommentDto;
import com.example.MyBookShopApp.data.api.VoteRequest;
import com.example.MyBookShopApp.data.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/bookReview")
    public ResponseEntity <?> postComment (@RequestBody CommentDto dto){
        return ResponseEntity.ok(commentService.postComment(dto));
    }

    @PostMapping(value = "/rateBookReview")
    public void vote (@RequestBody VoteRequest request){
        commentService.vote(request);
    }
}
