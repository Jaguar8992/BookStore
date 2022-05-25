package com.example.MyBookShopApp.data;


import com.example.MyBookShopApp.data.api.GradeRequest;
import com.example.MyBookShopApp.data.entities.Book;
import com.example.MyBookShopApp.data.entities.Grade;
import com.example.MyBookShopApp.data.repository.BookRepository;
import com.example.MyBookShopApp.data.repository.GradeRepository;
import com.example.MyBookShopApp.data.services.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookServiceTest {

    private final BookService bookService;
    private final BookRepository bookRepository;
    @MockBean
    private GradeRepository gradeRepository;

    @Autowired
    BookServiceTest(BookService bookService, BookRepository bookRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
    }

    @Test
    void getPageOfPopularBooks() {
        Page<Book> bookPage = bookService.getPageOfPopularBooks(0, 5);
        assertNotNull(bookPage);
        List<Book> bookList = bookPage.getContent();
        assertFalse(bookList.isEmpty());
        assertThat(bookList.size()).isGreaterThan(1);
        assertEquals(bookList.get(0).getId(), 1);
        assertEquals(bookList.get(1).getId(), 2);
        assertEquals(bookList.get(2).getId(), 3);
        assertEquals(bookList.get(3).getId(), 4);
        assertEquals(bookList.get(4).getId(), 5);
    }

    @Test
    void gradeBook (){
        List <Book> bookList = bookRepository.getWithoutGrade();
        assertThat(bookList.size()).isGreaterThan(1);
        Book book = bookList.get(0);
        assertEquals(0, book.gradeBook());
        GradeRequest request = new GradeRequest();
        request.setBookId(book.getId());
        request.setValue(1);

        ResponseEntity <String> result = bookService.rateBook(request);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("ok", result.getBody());

        Grade grade = new Grade();
        grade.setBook(book);
        grade.setValue(1);

        book.getGrades().add(grade);
        assertEquals(1, book.gradeBook());

        Grade secondGrade = new Grade();
        grade.setBook(book);
        grade.setValue(5);
        book.getGrades().add(secondGrade);
        assertEquals(2, book.gradeBook());
    }

    @Test
    void getRecommendedBooks (){
        Page <Book> bookPage = bookService.getPageOfRecommendedBooks(0, 5);
        assertNotNull(bookPage);
        assertFalse(bookPage.getContent().isEmpty());
        int maxValue = 5;
        for (Book book : bookPage.getContent()){
            assertEquals(maxValue, book.gradeBook());
        }
    }
}