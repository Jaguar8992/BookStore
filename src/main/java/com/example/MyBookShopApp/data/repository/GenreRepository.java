package com.example.MyBookShopApp.data.repository;


import com.example.MyBookShopApp.data.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    @Query("SELECT g FROM Genre g WHERE g.parent IS NULL")
    List<Genre> getParentGenres ();
}
