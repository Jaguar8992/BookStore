package com.example.MyBookShopApp.data.services;


import com.example.MyBookShopApp.data.api.TagDto;
import com.example.MyBookShopApp.data.entities.Book;
import com.example.MyBookShopApp.data.entities.Tag;
import com.example.MyBookShopApp.data.entities.TagSize;
import com.example.MyBookShopApp.data.repository.BookRepository;
import com.example.MyBookShopApp.data.repository.TagRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final BookRepository bookRepository;

    @Autowired
    public TagService(TagRepository tagRepository, BookRepository bookRepository) {
        this.tagRepository = tagRepository;
        this.bookRepository = bookRepository;
    }

    public List<TagDto> getTagList (){
        List <Tag> tags = tagRepository.findAll();
        int maxsize = 0;
        for (Tag tag : tags){
            int size = tag.getBooks().size();
            if (size > maxsize){
                maxsize = size;
            }
        }
        List <TagDto> response = new ArrayList<>();
        TagSize[] tagSizes = TagSize.values();
        int share = maxsize/tagSizes.length;
        if (share == 0){
            share = 1;
        }
        for (Tag tag : tags){
            int bookCount = tag.getBooks().size();
            String name = tag.getName();
            String size = null;
            for (int i = tagSizes.length; i >  0; i--){
                if (share * (i - 1) < bookCount && share * i >= bookCount){
                    size = tagSizes [i - 1].get();
                    break;
                }
            }
            if (size == null){
                size = TagSize.VERY_SMALL.get();
            }
            response.add(new TagDto(size, name, tag.getId()));
        }
        return response;
    }

    public Page<Book> getBooksForTag (Integer offset, Integer limit, long id){
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBooksByTag(id ,nextPage);
    }

    public Tag getTagById (long id) throws NotFoundException {
        return tagRepository.findById(id).orElseThrow(()->new NotFoundException("tag with id " + id + " dont found"));
    }
}
