package com.example.MyBookShopApp.data.repository;

import com.example.MyBookShopApp.data.entities.Author;
import com.example.MyBookShopApp.data.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findBooksByAuthor_FirstName(String name);

    @Query("from Book")
    List<Book> customFindAllBooks();

    //NEW BOOK REST REPOSITORY

    List<Book> findBooksByAuthorFirstNameContaining(String authorsFirstName);

    List<Book> findBooksByTitleContaining(String bookTitle);

    List<Book> findBooksByPriceOldBetween(Integer min, Integer max);

    List<Book> findBooksByPriceOldIs(Integer price);

    @Query("from Book where isBestseller=1")
    List<Book> getBestsellers();

    @Query(value = "SELECT * FROM books WHERE discount = (SELECT MAX(discount) FROM books", nativeQuery = true)
    List<Book> getBooksWithMaxDiscount();

    Page<Book> findBookByTitleContaining(String bookTitle, Pageable nextPage);

    Book findBookBySlug(String slug);

    List<Book> findBooksBySlugIn(String[] slugs);

    @Query("SELECT b FROM Book b order by (b.buyCount + 0.7 * b.cartCount + 0.4 * b.postponedCount)")
    Page <Book> getPopularBooks (Pageable nextPage);

    @Query("SELECT b FROM Book b WHERE b.grades.size = 0")
    List <Book> getWithoutGrade ();

    @Query("SELECT b FROM Book b JOIN Grade g ON g.book = b GROUP BY b.id ORDER BY (sum(g.value) / b.grades.size) DESC")
    Page <Book> findRecommendedBooks (Pageable page);

    @Query("SELECT DISTINCT b FROM Book b WHERE b in (SELECT a FROM Book a WHERE a.genre.id = :id) OR " +
            "b in (SELECT c FROM Book c WHERE c.genre.parent.id = :id) OR " +
            "b in (SELECT d FROM Book d WHERE d.genre.parent.parent.id = :id)")
    Page <Book> findByGenre(@Param("id") long id, Pageable nextPage);

    Page <Book> findBooksByAuthorIs(Author author, Pageable nextPage);

    @Query("SELECT DISTINCT b FROM Book b WHERE b.pubDate >= :from AND b.pubDate <= :to")
    Page <Book> findBookByDate (@Param("from") Date from, @Param ("to") Date to, Pageable page);

    @Query("SELECT b FROM Book b ORDER BY (b.buyCount + b.inBasket + b.inPending) DESC")
    Page <Book> findPopular (Pageable page);

    @Query("SELECT b FROM Book b JOIN TagToBook tb ON tb.bookId = b.id WHERE tb.tagId = :id")
    Page <Book> findBooksByTag (@Param("id") long id, Pageable nextPage);

}
