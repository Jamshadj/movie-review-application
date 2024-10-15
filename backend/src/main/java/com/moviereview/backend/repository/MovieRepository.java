package com.moviereview.backend.repository;

import com.moviereview.backend.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByUserId(Long userId);

    @Query("SELECT m FROM Movie m LEFT JOIN m.reviews r GROUP BY m.id ORDER BY COALESCE(AVG(r.starCount), 0) DESC")
    Page<Movie> findTopRatedMovies(Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE upper(m.title) LIKE upper(concat('%', ?1, '%'))")
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    @Query("SELECT DISTINCT m.genre FROM Movie m")
    List<String> findDistinctGenres();

    @Query("SELECT DISTINCT m.language FROM Movie m")
    List<String> findDistinctLanguages();
}
