package com.example.MyBookShopApp.data.entities;

import io.swagger.annotations.ApiModel;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tag")
@ApiModel(description = "tag entity")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @ManyToMany (cascade = CascadeType.ALL)
    @JoinTable (name = "tag2book",
            joinColumns = {@JoinColumn (name = "tag_id")},
            inverseJoinColumns = {@JoinColumn (name = "book_id")})
    private List<Book> books;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", books=" + books +
                '}';
    }
}
