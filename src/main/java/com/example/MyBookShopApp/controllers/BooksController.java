package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.anotations.FileDownloadLog;
import com.example.MyBookShopApp.anotations.LogMethod;
import com.example.MyBookShopApp.data.entities.Book;
import com.example.MyBookShopApp.data.repository.BookRepository;
import com.example.MyBookShopApp.data.ResourceStorage;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.security.BookstoreUser;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookRepository bookRepository;
    private final ResourceStorage storage;
    private final BookstoreUserRegister userRegister;
    private final BookService bookService;


    @Autowired
    public BooksController(BookRepository bookRepository, ResourceStorage storage, BookstoreUserRegister userRegister, BookService bookService) {
        this.bookRepository = bookRepository;
        this.storage = storage;
        this.userRegister = userRegister;
        this.bookService = bookService;
    }

    @GetMapping("/{slug}")
    public String bookPage(@PathVariable("slug") String slug, Model model) {
        Book book = bookRepository.findBookBySlug(slug);
        bookService.fillSlugModel(model, book);
        model.addAttribute("slugBook", book);
        try {
            BookstoreUser user = userRegister.getCurrentUser();
            model.addAttribute("curUsr",user);
        } catch (Exception ex){
            return "/books/slug";
        }
        return "/books/slugmy";
    }

    @PostMapping("/{slug}/img/save")
    @LogMethod
    public String saveNewBookImage(@RequestParam("file")MultipartFile file,@PathVariable("slug")String slug) throws IOException {
        String savePath = storage.saveNewBookImage(file,slug);
        Book bookToUpdate = bookRepository.findBookBySlug(slug);
        bookToUpdate.setImage(savePath);
        bookRepository.save(bookToUpdate); //save new path in db here

        return "redirect:/books/"+slug;
    }

    @GetMapping("/download/{hash}")
    @FileDownloadLog
    public ResponseEntity<ByteArrayResource> bookFile(@PathVariable("hash")String hash) throws IOException{
        Path path = storage.getBookFilePath(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("book file path: "+path);

        MediaType mediaType = storage.getBookFileMime(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("book file mime type: "+mediaType);

        byte[] data = storage.getBookFileByteArray(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("book file data len: "+data.length);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+path.getFileName().toString())
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }
}
