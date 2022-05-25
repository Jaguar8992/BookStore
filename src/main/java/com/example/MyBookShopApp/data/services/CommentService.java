package com.example.MyBookShopApp.data.services;


import com.example.MyBookShopApp.data.api.CommentDto;
import com.example.MyBookShopApp.data.api.VoteRequest;
import com.example.MyBookShopApp.data.entities.Book;
import com.example.MyBookShopApp.data.entities.Comment;
import com.example.MyBookShopApp.data.repository.BookRepository;
import com.example.MyBookShopApp.data.repository.CommentRepository;
import com.example.MyBookShopApp.security.BookstoreUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, BookRepository bookRepository) {
        this.commentRepository = commentRepository;
        this.bookRepository = bookRepository;
    }

    public String postComment (CommentDto dto){
        Book book = bookRepository.getById(dto.getBookId());
        Comment comment = new Comment();
        comment.setBook(book);
        comment.setText(dto.getText());
        comment.setDate(new Date());
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof DefaultOidcUser){
            DefaultOidcUser user = (DefaultOidcUser) principal;
            comment.setName(user.getAttribute("name"));
        } else {
            BookstoreUserDetails user =
                    (BookstoreUserDetails) principal;
            comment.setName(user.getUsername());
        }
        commentRepository.save(comment);
        return "ok";
    }

    public void vote (VoteRequest request){
        Comment comment = commentRepository.getById(request.getReviewid());
        if (request.getValue() == -1){
            comment.setDislikes(comment.getDislikes() + 1);
        }
        if (request.getValue() == 1){
            comment.setLikes(comment.getLikes() + 1);
        }
        commentRepository.save(comment);
    }
}
