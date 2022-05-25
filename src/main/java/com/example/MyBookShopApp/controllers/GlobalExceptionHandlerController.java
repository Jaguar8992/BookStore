package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.errs.BookstoreUserExistException;
import com.example.MyBookShopApp.errs.EmptySearchException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(EmptySearchException.class)
    public String handleEmptySearchException(EmptySearchException e, RedirectAttributes redirectAttributes){
        Logger.getLogger(this.getClass().getSimpleName()).warning(e.getLocalizedMessage());
        redirectAttributes.addFlashAttribute("searchError",e);
        return "redirect:/";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String userNotFound(IllegalArgumentException e, HttpServletRequest request){
        Logger.getLogger(this.getClass().getSimpleName()).warning(e.getLocalizedMessage());
        for (Cookie cookie : request.getCookies()){
            cookie.setMaxAge(0);
        }
        return "redirect:/";
    }

    @ExceptionHandler(BookstoreUserExistException.class)
    public String bookstoreUserExistException(BookstoreUserExistException e, RedirectAttributes redirectAttributes){
        Logger.getLogger(this.getClass().getSimpleName()).warning(e.getLocalizedMessage());
        redirectAttributes.addFlashAttribute("regFalse", true);
        return "redirect:/signup";
    }
}
