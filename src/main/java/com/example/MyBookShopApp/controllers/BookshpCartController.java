package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.anotations.LogMethod;
import com.example.MyBookShopApp.data.entities.Book;
import com.example.MyBookShopApp.data.repository.BookRepository;
import com.example.MyBookShopApp.data.services.PaymentService;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Logger;

@Controller
@RequestMapping("/books")
public class BookshpCartController {

    @ModelAttribute(name = "bookCart")
    public List<Book> bookCart() {
        return new ArrayList<>();
    }

    private final BookRepository bookRepository;
    private final PaymentService paymentService;
    private final BookstoreUserRegister register;
    private Logger log = Logger.getLogger(BookshpCartController.class.getName());

    @Autowired
    public BookshpCartController(BookRepository bookRepository, PaymentService paymentService, BookstoreUserRegister register) {
        this.bookRepository = bookRepository;
        this.paymentService = paymentService;
        this.register = register;
    }

    @GetMapping("/cart")
    @LogMethod
    public String handleCartRequest(@CookieValue(value = "cartContents", required = false) String cartContents,
                                    Model model) {
        double totalCost = 0;
        if (cartContents == null || cartContents.equals("")) {
            model.addAttribute("isCartEmpty", true);
        } else {
            model.addAttribute("isCartEmpty", false);
            cartContents = cartContents.startsWith("/") ? cartContents.substring(1) : cartContents;
            cartContents = cartContents.endsWith("/") ? cartContents.substring(0, cartContents.length() - 1) :
                    cartContents;
            String[] cookieSlugs = cartContents.split("/");
            List<Book> booksFromCookieSlugs = bookRepository.findBooksBySlugIn(cookieSlugs);
            model.addAttribute("bookCart", booksFromCookieSlugs);
            int totalOldPrice = booksFromCookieSlugs.stream().map(Book::getPriceOld).reduce(Integer::sum).orElse(0);
            int discountPrice = booksFromCookieSlugs.stream().map(Book::discountPrice).reduce(Integer::sum).orElse(0);
            model.addAttribute("total", totalOldPrice);
            model.addAttribute("discount", discountPrice);
            totalCost = booksFromCookieSlugs.stream().mapToDouble(Book::discountPrice).sum();
        }
        double balance = 0;
        try {
          balance = register.getCurrentUser().getBalance();
          model.addAttribute("notEnough", balance < totalCost);
        } catch (Exception ex) {
            log.info("cant get balance");
        }

        return "cart";
    }

    @PostMapping("/unlink/changeBookStatus/cart/remove/{slug}")
    @LogMethod
    public String handleRemoveBookFromCartRequest(@PathVariable("slug") String slug, @CookieValue(name =
            "cartContents", required = false) String cartContents, HttpServletResponse response, Model model) {
        if (cartContents != null && !cartContents.equals("")) {
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(cartContents.split("/")));
            cookieBooks.remove(slug);
            Cookie cookie = new Cookie("cartContents", String.join("/", cookieBooks));
            cookie.setPath("/books");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);
        } else {
            model.addAttribute("isCartEmpty", true);
        }

        return "redirect:/books/cart";
    }

    @PostMapping("cart/changeBookStatus/{slug}")
    @LogMethod
    public String handleChangeBookStatus(@PathVariable("slug") String slug, @CookieValue(name = "cartContents",
            required = false) String cartContents, @CookieValue(name = "postponedContents",
            required = false) String postponedContents, HttpServletResponse response, Model model) {
        if (postponedContents != null && !postponedContents.equals("")){
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(postponedContents.split("/")));
            cookieBooks.remove(slug);
            Cookie cookie = new Cookie("postponedContents", String.join("/", cookieBooks));
            cookie.setPath("/books");
            response.addCookie(cookie);
            model.addAttribute("isPostponedEmpty", false);
        }
        if (cartContents == null || cartContents.equals("")) {
            Cookie cookie = new Cookie("cartContents", slug);
            cookie.setPath("/books");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);
        } else if (!cartContents.contains(slug)) {
            StringJoiner stringJoiner = new StringJoiner("/");
            stringJoiner.add(cartContents).add(slug);
            Cookie cookie = new Cookie("cartContents", stringJoiner.toString());
            cookie.setPath("/books");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);
        }

        return "redirect:/books/" + slug;
    }

    @GetMapping("/pay")
    public RedirectView handlePay(@CookieValue(value = "cartContents", required = false) String cartContents, HttpServletResponse response, Model model) throws NoSuchAlgorithmException {
        String paymentUrl = paymentService.payBooks(cartContents, model, response);
        return new RedirectView(paymentUrl);
    }
}
