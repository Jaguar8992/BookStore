package com.example.MyBookShopApp.data.entities;

import com.example.MyBookShopApp.data.DateParser;
import com.example.MyBookShopApp.data.entities.Book;
import com.example.MyBookShopApp.security.BookstoreUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    private String id;

    @Column(nullable = false)
    private Double sum;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private BookstoreUser user;

    @Column(nullable = false)
    private String description;


    @Column (nullable = false, columnDefinition = "timestamp default current_timestamp with time zone", updatable = false)
    @CreationTimestamp
    private Date date;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "book_id")
    private Book book;

    public Transaction() {
    }

    public Transaction(String id, Double sum, BookstoreUser user, String description) {
        this.id = id;
        this.sum = sum;
        this.user = user;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public BookstoreUser getUser() {
        return user;
    }

    public void setUser(BookstoreUser user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime(){
        return DateParser.parseDate(date);
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
