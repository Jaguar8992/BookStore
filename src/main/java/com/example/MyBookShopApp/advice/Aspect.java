package com.example.MyBookShopApp.advice;

import com.example.MyBookShopApp.data.entities.Author;
import com.example.MyBookShopApp.data.entities.Book;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@org.aspectj.lang.annotation.Aspect
@Component
public class Aspect {

    private Long durationMills;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Around(value = "@annotation(com.example.MyBookShopApp.anotations.LogMethod))")
    public Object aroundDurationTrackingAdvice(ProceedingJoinPoint proceedingJoinPoint) {
        durationMills = new Date().getTime();
        logger.info(proceedingJoinPoint.toString() + " duration tracking begins");
        Object returnValue = null;
        try {
            returnValue = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            logger.info(throwable.getMessage());
        }

        durationMills = new Date().getTime() - durationMills;
        logger.info(proceedingJoinPoint.toString() + " execution took: " + durationMills + " mills");
        logger.info(proceedingJoinPoint.toString() + " was redirected to " + returnValue);
        return returnValue;
    }

    @Around(value = "within(com.example.MyBookShopApp.data.services*))")
    public Object serviceTrackingAdvice(ProceedingJoinPoint proceedingJoinPoint) {
        durationMills = new Date().getTime();
        logger.info(proceedingJoinPoint.toString() + " duration tracking begins");
        Object returnValue = null;
        try {
            returnValue = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            logger.info(throwable.getMessage());
        }

        durationMills = new Date().getTime() - durationMills;
        logger.info(proceedingJoinPoint.toString() + " execution took: " + durationMills + " mills");
        logger.info(proceedingJoinPoint.toString() + " was redirected to " + returnValue);
        return returnValue;
    }

    @Around(value = "@annotation(com.example.MyBookShopApp.anotations.FileDownloadLog))")
    public Object safeFileTrackingAdvice(ProceedingJoinPoint proceedingJoinPoint) {
        durationMills = new Date().getTime();
        logger.info(proceedingJoinPoint.toString() + " duration tracking begins");
        ResponseEntity returnValue = null;
        try {
            returnValue = (ResponseEntity) proceedingJoinPoint.proceed();
            logger.info("status " + returnValue.getStatusCode());
            ByteArrayResource bytes = (ByteArrayResource) returnValue.getBody();
            logger.info("fileName " + bytes.getFilename());
            logger.info("file size " + bytes.contentLength());
        } catch (Throwable throwable) {
            logger.info(throwable.getMessage());
        }

        durationMills = new Date().getTime() - durationMills;
        logger.info(proceedingJoinPoint.toString() + " execution took: " + durationMills + " mills");
        return returnValue;
    }

    @Pointcut(value = "within(com.example.MyBookShopApp.data.services.AuthorService)")
    public void authorServicePointcut() {
    }

    @Before("authorServicePointcut()")
    public void authorServiceStartAdvice (JoinPoint joinPoint){
        durationMills = new Date().getTime();
        logger.info(joinPoint.toShortString());
    }

    @AfterReturning(pointcut = "authorServicePointcut()", returning = "response")
    public void authorsCatcherAdvice (JoinPoint joinPoint, Map<String, List<Author>> response){
        logger.info(joinPoint.toShortString() + " was found " + response.size() + " authors, duration " + (new Date().getTime() - durationMills) + " mills");
    }

    @AfterThrowing(pointcut = "authorServicePointcut()", throwing = "ex")
    public void authorsThrowingAdvice (Exception ex){
        logger.info(ex.getMessage());
    }

    @Pointcut(value = "within(com.example.MyBookShopApp.data.services.BookService)")
    public void bookServicePointcut() {
    }

    @Before("bookServicePointcut()")
    public void booksServiceStartAdvice (JoinPoint joinPoint){
        durationMills = new Date().getTime();
        logger.info(joinPoint.toShortString());
    }

    @AfterReturning(pointcut = "bookServicePointcut()", returning = "response")
    public void booksCatcherAdvice (JoinPoint joinPoint, Iterable <Book> response){
        List <Book> books = null;
        if (response instanceof List){
            books = (List<Book>) response;
        }
        if (response instanceof Page){
            books = ((Page<Book>) response).getContent();
        }
        logger.info(joinPoint.toString() + " was found " + books.size() + " books, duration " + (new Date().getTime() - durationMills) + " mills");
    }

    @AfterThrowing(pointcut = "bookServicePointcut()", throwing = "ex")
    public void booksThrowingAdvice (Exception ex){
        logger.info(ex.getMessage());
    }
}
