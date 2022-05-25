package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.entities.Author;
import com.example.MyBookShopApp.data.entities.Book;
import com.example.MyBookShopApp.data.repository.AuthorRepository;
import com.example.MyBookShopApp.data.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private JdbcTemplate jdbcTemplate;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Autowired
    public AuthorService(JdbcTemplate jdbcTemplate, AuthorRepository authorRepository, BookRepository bookRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public Map<String, List<Author>> getAuthorsMap() {
        List<Author> authors = jdbcTemplate.query("SELECT * FROM authors",(ResultSet rs, int rowNum) -> {
            Author author = new Author();
            author.setId(rs.getInt("id"));
            author.setFirstName(rs.getString("first_name"));
            author.setLastName(rs.getString("last_name"));
            return author;
        });

        return authors.stream().collect(Collectors.groupingBy((Author a) -> {return a.getLastName().substring(0,1);}));
    }

    public void insertDataIntoModel (int id, Model model){
        Author author = authorRepository.getById(id);
        String [] biographyMassive = author.getBiography().split(" ");
        List <String> strings = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < biographyMassive.length; i++){
            builder.append(biographyMassive [i] + " ");
            if (biographyMassive.length - i < 15){
                strings.add(builder.toString());
                break;
            }
            if (i % 15 == 0){
                strings.add(builder.toString());
                builder = new StringBuilder();
            }
        }
        List <String> firstStrings = new ArrayList<>(strings.subList(0, 1));
        strings.remove(0);
        strings.remove(1);

        model.addAttribute("name", author.getFirstName() + " " + author.getLastName());
        model.addAttribute("firstStrings", firstStrings);
        model.addAttribute("strings", strings);
        model.addAttribute("photo", author.getPhoto());
        model.addAttribute("count", author.getBookList().size());
        model.addAttribute("bookByAuthor", getBooksByAuthor(id, 0, 6).getContent());
        model.addAttribute("id", author.getId());
    }

    public Page<Book> getBooksByAuthor (int id, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset / limit, limit);
        Author author = authorRepository.getById(id);
        return bookRepository.findBooksByAuthorIs(author, nextPage);
    }

    public void addDataForAuthorBookPage(int id, Model model){
        model.addAttribute("bookForAuthor", getBooksByAuthor(id, 0, 6).getContent());
        Author author = authorRepository.getOne(id);
        model.addAttribute("authorName", author.getFirstName() + " " + author.getLastName());
    }
}
