package com.moviereview.backend.dto;

import com.moviereview.backend.model.Movie;

import java.util.List;

public class MovieResponse {
    private List<Movie> movies;
    private List<String> distinctGenres;
    private List<String> distinctLanguages;
    private int totalPages; // Add this field

    public MovieResponse(List<Movie> movies, List<String> distinctGenres, List<String> distinctLanguages, int totalPages) {
        this.movies = movies;
        this.distinctGenres = distinctGenres;
        this.distinctLanguages = distinctLanguages;
        this.totalPages = totalPages;
    }

    // Getters and Setters

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public List<String> getDistinctGenres() {
        return distinctGenres;
    }

    public void setDistinctGenres(List<String> distinctGenres) {
        this.distinctGenres = distinctGenres;
    }

    public List<String> getDistinctLanguages() {
        return distinctLanguages;
    }

    public void setDistinctLanguages(List<String> distinctLanguages) {
        this.distinctLanguages = distinctLanguages;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
