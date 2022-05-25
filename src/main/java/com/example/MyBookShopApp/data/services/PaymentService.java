package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.entities.Book;
import com.example.MyBookShopApp.data.entities.PurchaseBookKey;
import com.example.MyBookShopApp.data.entities.PurchasedBook;
import com.example.MyBookShopApp.data.entities.Transaction;
import com.example.MyBookShopApp.data.repository.BookRepository;
import com.example.MyBookShopApp.data.repository.PurchasedBookRepository;
import com.example.MyBookShopApp.data.repository.TransactionRepository;
import com.example.MyBookShopApp.security.BookstoreUser;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import com.example.MyBookShopApp.security.BookstoreUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    @Value("${robokassa.merchant.login}")
    private String merchantLogin;

    @Value("${robokassa.pass.first.test}")
    private String firstTestPass;
    private final BookstoreUserRegister register;
    private final BookRepository bookRepository;
    private final TransactionRepository transactionRepository;
    private final BookstoreUserRepository bookstoreUserRepository;
    private final PurchasedBookRepository purchasedBookRepository;

    @Autowired
    public PaymentService(BookstoreUserRegister register, BookRepository bookRepository, TransactionRepository transactionRepository, BookstoreUserRepository bookstoreUserRepository, PurchasedBookRepository purchasedBookRepository) {
        this.register = register;
        this.bookRepository = bookRepository;
        this.transactionRepository = transactionRepository;
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.purchasedBookRepository = purchasedBookRepository;
    }


    public String payBooks(String cartContents, Model model, HttpServletResponse response) throws NoSuchAlgorithmException {
        cartContents = cartContents.startsWith("/") ? cartContents.substring(1) : cartContents;
        cartContents = cartContents.endsWith("/") ? cartContents.substring(0, cartContents.length() - 1) :
                cartContents;
        String[] cookieSlugs = cartContents.split("/");
        List<Book> booksFromCookieSlugs = bookRepository.findBooksBySlugIn(cookieSlugs);
        Double paymentSumTotal = booksFromCookieSlugs.stream().mapToDouble(Book::discountPrice).sum();
        BookstoreUser user;
        try {
            user = register.getCurrentUser();
        } catch (Exception e){
            return "/signin";
        }
        Double balance = user.getBalance();
        if (balance < paymentSumTotal){
            model.addAttribute("notEnough", true);
            return "cart";
        }
        List <Transaction> transactions = new ArrayList<>();
        List <PurchasedBook> purchasedBooks = new ArrayList<>();
        for (Book book : booksFromCookieSlugs) {
            String invId = UUID.randomUUID().toString().replace("-", "");
            double price = book.discountPrice().doubleValue();
            Transaction transaction = new Transaction(invId, -price, user, "Покупка книги");
            transaction.setBook(book);
            transactions.add(transaction);
            PurchasedBook purchasedBook = new PurchasedBook(new PurchaseBookKey(user.getId(), book.getId()));
            purchasedBooks.add(purchasedBook);
        }
        transactionRepository.saveAll(transactions);
        purchasedBookRepository.saveAll(purchasedBooks);

        response.addCookie(new Cookie("cartContents", ""));
        model.addAttribute("success", true);
        model.addAttribute("isCartEmpty", true);
        return "cart";


/*        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update((merchantLogin + ":" + paymentSumTotal.toString() + ":" + invId + ":" + firstTestPass).getBytes());
        return "https://auth.robokassa.ru/Merchant/Index.aspx"+
                "?MerchantLogin="+merchantLogin+
                "&IndId="+invId+
                "&Culture=ru"+
                "&Encoding=utf-8"+
                "&OutSum="+paymentSumTotal.toString()+
                "&SignatureValue="+ DatatypeConverter.printHexBinary(md.digest()).toUpperCase()+
                "&IsTest=1";*/

    }
}
