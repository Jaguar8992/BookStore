package com.example.MyBookShopApp.data.entities;

public enum TagSize {
    VERY_SMALL("Tag_xs"),
    SMALL("Tag_sm"),
    MEDIUM("Tag"),
    MIDDLE("Tag_md"),
    BIG("Tag_lg");

    private String size;

    TagSize(String size) {
        this.size = size;
    }

    public String get() {
        return size;
    }
}
