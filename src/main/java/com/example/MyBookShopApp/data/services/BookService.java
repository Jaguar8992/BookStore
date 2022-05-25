package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.api.GradeRequest;
import com.example.MyBookShopApp.data.entities.Author;
import com.example.MyBookShopApp.data.entities.Book;
import com.example.MyBookShopApp.data.entities.Grade;
import com.example.MyBookShopApp.data.google.api.books.Item;
import com.example.MyBookShopApp.data.google.api.books.Root;
import com.example.MyBookShopApp.data.repository.BookRepository;
import com.example.MyBookShopApp.data.repository.GradeRepository;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class BookService {

    private BookRepository bookRepository;
    private RestTemplate restTemplate;
    private final GradeRepository gradeRepository;

    @Autowired
    public BookService(BookRepository bookRepository, RestTemplate restTemplate, GradeRepository gradeRepository) {
        this.bookRepository = bookRepository;
        this.restTemplate = restTemplate;
        this.gradeRepository = gradeRepository;
    }

    public List<Book> getBooksData() {
        return bookRepository.findAll();
    }

    //NEW BOOK SERVICE METHODS

    public List<Book> getBooksByAuthor(String authorName) {
        return bookRepository.findBooksByAuthorFirstNameContaining(authorName);
    }

    public List<Book> getBooksByTitle(String title) throws BookstoreApiWrongParameterException {
        if (title.equals("") || title.length() <= 1) {
            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        } else {
            List<Book> data = bookRepository.findBooksByTitleContaining(title);
            if (data.size() > 0) {
                return data;
            } else {
                throw new BookstoreApiWrongParameterException("No data found with specified parameters...");
            }
        }
    }

    public List<Book> getBooksWithPriceBetween(Integer min, Integer max) {
        return bookRepository.findBooksByPriceOldBetween(min, max);
    }

    public List<Book> getBooksWithPrice(Integer price) {
        return bookRepository.findBooksByPriceOldIs(price);
    }

    public List<Book> getBooksWithMaxPrice() {
        return bookRepository.getBooksWithMaxDiscount();
    }

    public List<Book> getBestsellers() {
        return bookRepository.getBestsellers();
    }

    public Page<Book> getPageofRecommendedBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findAll(nextPage);
    }

    public Page<Book> getPageOfSearchResultBooks(String searchWord, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBookByTitleContaining(searchWord, nextPage);
    }

    @Value("${google.books.api.key}")
    private String apiKey;

    public Page<Book> getPageOfPopularBooks(Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);
        return bookRepository.getPopularBooks(nextPage);
    }

    public Page<Book> getPageOfRecommendedBooks(Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);
        return bookRepository.findRecommendedBooks(nextPage);
    }


    public ResponseEntity<String> rateBook(GradeRequest request) {
        Book book = bookRepository.getById(request.getBookId());
        Grade grade = new Grade();
        grade.setBook(book);
        grade.setValue(request.getValue());
        gradeRepository.save(grade);

        return ResponseEntity.ok("ok");
    }

    public List<Book> getPageOfGoogleBooksApiSearchResult(String searchWord, Integer offset, Integer limit) {
        String REQUEST_URL = "https://www.googleapis.com/books/v1/volumes" +
                "?q=" + searchWord +
                "&key=" + apiKey +
                "&filter=paid-ebooks" +
                "&startIndex=" + offset +
                "&maxResult=" + limit;

        Root root =restTemplate.getForEntity(REQUEST_URL,Root.class).getBody();
        ArrayList<Book> list = new ArrayList<>();
        if(root != null){
            for (Item item:root.getItems()){
                Book book = new Book();
                if(item.getVolumeInfo()!=null){
                    book.setAuthor(new Author(item.getVolumeInfo().getAuthors()));
                    book.setTitle(item.getVolumeInfo().getTitle());
                    book.setImage(item.getVolumeInfo().getImageLinks().getThumbnail());
                }
                if(item.getSaleInfo()!=null){
                    book.setPrice(item.getSaleInfo().getRetailPrice().getAmount());
                    Double oldPrice = item.getSaleInfo().getListPrice().getAmount();
                    book.setPriceOld(oldPrice.intValue());
                }
                list.add(book);
            }
        }
        return list;
    }


    public void fillSlugModel(Model model, Book book) {
        int rate = book.gradeBook();
        List<String> isActiveStar = IntStream.range(0, 5).mapToObj(number -> number < rate ? " Rating-star_view" : "").collect(Collectors.toList());
        model.addAttribute("slugBook", book);
        model.addAttribute("isActiveStar", isActiveStar);
        List<Grade> grades = book.getGrades();
        int fiveStars = 0;
        int fourStars = 0;
        int threeStars = 0;
        int twoStars = 0;
        int oneStar = 0;
        if (grades != null) {
            Map<Integer, List<Grade>> sorted = grades.stream().collect(Collectors.groupingBy(Grade::getValue));
            for (int i = 1; i <= 5; i++) {
                List<Grade> gradeList = sorted.get(i);
                if (gradeList != null && !gradeList.isEmpty()) {
                    if (i == 1) {
                        oneStar = gradeList.size();
                    }
                    if (i == 2) {
                        twoStars = gradeList.size();
                    }
                    if (i == 3) {
                        threeStars = gradeList.size();
                    }
                    if (i == 4) {
                        fourStars = gradeList.size();
                    }
                    if (i == 5) {
                        fiveStars = gradeList.size();
                    }
                }
            }
        }
        model.addAttribute("oneStar", oneStar);
        model.addAttribute("twoStar", twoStars);
        model.addAttribute("threeStar", threeStars);
        model.addAttribute("fourStar", fourStars);
        model.addAttribute("fiveStar", fiveStars);
    }

    public Page <Book> getSortingPage (int offset, int limit, Sort sort){
        Pageable nextPage = PageRequest.of(offset,limit, sort);
        return bookRepository.findAll(nextPage);
    }

    public Page <Book> getPopularPage (int offset, int limit){
        Pageable nextPage = PageRequest.of(offset,limit);
        return bookRepository.findPopular(nextPage);
    }
}
