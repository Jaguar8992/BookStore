package com.example.MyBookShopApp.data.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class GenreDto {
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long count;
    private long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<GenreDto> childs;

    public GenreDto(String name, Long count, long id) {
        this.name = name;
        this.count = count;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<GenreDto> getChilds() {
        return childs;
    }

    public void setChilds(List<GenreDto> childs) {
        this.childs = childs;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
