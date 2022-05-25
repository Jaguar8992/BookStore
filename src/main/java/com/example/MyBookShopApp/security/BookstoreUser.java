package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.entities.Book;
import com.example.MyBookShopApp.data.entities.Transaction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class BookstoreUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Transaction> transactions;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "purchased_book",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id"))
    private List<Book> purchaseBook;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Transaction> getTransactions() {
        if (transactions == null){
            return new ArrayList<>();
        }
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Double getBalance(){
        return transactions.stream().map(Transaction::getSum).reduce(Double::sum).orElse(0.0);
    }

    public List<Book> getPurchaseBook() {
        return purchaseBook;
    }

    public void putBooks (List <Book> books){
        books.addAll(books);
    }

    public void setPurchaseBook(List<Book> purchaseBook) {
        this.purchaseBook = purchaseBook;
    }
}
