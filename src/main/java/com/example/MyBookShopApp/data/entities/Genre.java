package com.example.MyBookShopApp.data.entities;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table (name = "genre")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Genre parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, orphanRemoval = true)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<Genre> childs;

    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List <Book> books;

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

    public Genre getParent() {
        return parent;
    }

    public void setParent(Genre parent) {
        this.parent = parent;
    }

    public List<Genre> getChilds() {
        return childs;
    }

    public void setChilds(List<Genre> childs) {
        this.childs = childs;
    }

    public List<Book> getBooks() {
        if (childs != null && !childs.isEmpty()){
            return childs.stream().flatMap(genre -> genre.getBooks().stream()).collect(Collectors.toList());
        }
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public boolean haveAChild (){
        if (childs != null && !childs.isEmpty()){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parent=" + parent +
                ", childs=" + childs +
                '}';
    }
}
