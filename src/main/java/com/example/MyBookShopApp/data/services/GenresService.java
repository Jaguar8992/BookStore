package com.example.MyBookShopApp.data.services;


import com.example.MyBookShopApp.data.api.GenreDto;
import com.example.MyBookShopApp.data.entities.Book;
import com.example.MyBookShopApp.data.entities.Genre;
import com.example.MyBookShopApp.data.repository.BookRepository;
import com.example.MyBookShopApp.data.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenresService {

    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;

    @Autowired
    public GenresService(GenreRepository genreRepository, BookRepository bookRepository) {
        this.genreRepository = genreRepository;
        this.bookRepository = bookRepository;
    }

    public List<GenreDto> getGenres (){
        List<Genre> parents = genreRepository.getParentGenres();
        List <GenreDto> response = new ArrayList<>();
        parents.forEach(genre -> {
            response.add(getDto(genre));
        });
        return response;
    }

    private GenreDto getDto (Genre genre){
        long count = genre.getBooks().size();
        GenreDto dto = new GenreDto(genre.getName(), count, genre.getId());
        List <GenreDto> childs = new ArrayList<>();
        if (genre.getChilds() != null && !genre.getChilds().isEmpty()){
            List <Genre> genreChilds = genre.getChilds();
            genreChilds.forEach(child -> {
                childs.add(getDto(child));
            });
        }
        if (!childs.isEmpty()){
            dto.setChilds(childs);
        }
        return dto;
    }

    public Page<Book> getBooksByGenre (long id, Integer offset, Integer limit, Model model) {
        Genre genre = genreRepository.getById(id);
        if (genre.getParent() != null){
            Genre parent = genre.getParent();
            model.addAttribute("genreId", genre.getId());
            if (parent.getParent() != null){
                Genre grandParent = parent.getParent();
                model.addAttribute("childId", genre.getId());
                model.addAttribute("middleId", parent.getId());
                model.addAttribute("parentId", grandParent.getId());
                model.addAttribute("childName", genre.getName());
                model.addAttribute("middleName", parent.getName());
                model.addAttribute("parentName", grandParent.getName());
            } else {
                model.addAttribute("parentId", parent.getId());
                model.addAttribute("middleId", genre.getId());
                model.addAttribute("parentName", parent.getName());
                model.addAttribute("middleName", genre.getName());
            }
        } else {
            model.addAttribute("parentId", genre.getId());
            model.addAttribute("parentName", genre.getName());
        }
        model.addAttribute("genreName", genre.getName());
        model.addAttribute("genreId", genre.getId());
        return getPageByGenre(id, offset, limit);
    }

    public Page<Book> getPageByGenre (long id, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset / limit, limit);
        return bookRepository.findByGenre(id, nextPage);
    }

}
