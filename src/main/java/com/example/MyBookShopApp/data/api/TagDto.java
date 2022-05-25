package com.example.MyBookShopApp.data.api;

public class TagDto {

    private String tagSize;
    private String name;
    private long id;

    public TagDto(String tagSize, String name, long id) {
        this.tagSize = tagSize;
        this.name = name;
        this.id = id;
    }

    public String getTagSize() {
        return tagSize;
    }

    public void setTagSize(String tagSize) {
        this.tagSize = tagSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
