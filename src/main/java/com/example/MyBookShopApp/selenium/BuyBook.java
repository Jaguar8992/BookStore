package com.example.MyBookShopApp.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class BuyBook {
    private String url = "http://localhost:8085/";
    private ChromeDriver driver;

    public BuyBook(ChromeDriver driver) {
        this.driver = driver;
    }

    public BuyBook callPage() {
        driver.get(url);
        return this;
    }

    public BuyBook pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }

    public BuyBook cardTitleSubmit() {
        WebElement element = driver.findElement(By.className("Middle-main"));
        List<WebElement> elementList = element.findElements(By.className("Card-content"));
        elementList.get(0).findElement(By.className("Card-title")).click();
        return this;
    }

    public BuyBook buy() {
        List <WebElement> elements = driver.findElements(By.className("ProductCard-cartElement"));
        elements.get(1).click();
        return this;
    }

    public BuyBook toCart () {
        List <WebElement> elements = driver.findElements(By.className("CartBlock-block"));
        elements.get(1).click();
        return this;
    }

    public BuyBook removeFromCart (){
        List <WebElement> elements = driver.findElements(By.className("Cart-btn"));
        elements.get(1).click();
        return this;
    }


}
