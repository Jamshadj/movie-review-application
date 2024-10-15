package com.moviereview.backend.service;

import com.moviereview.backend.model.Movie;
import com.moviereview.backend.model.Review;
import com.moviereview.backend.repository.MovieRepository;
import com.moviereview.backend.repository.ReviewRepository;
import com.moviereview.backend.dto.MovieResponse; // Ensure you import your new DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public List<Movie> getMoviesByUserId(Long userId) {
        return movieRepository.findByUserId(userId);
    }

    public Optional<Movie> getMovieById(Long movieId) {
        return movieRepository.findById(movieId);
    }

    public Review addReview(Long movieId, Review review) {
        Optional<Movie> movieOptional = movieRepository.findById(movieId);
        if (movieOptional.isPresent()) {
            review.setMovie(movieOptional.get());
            return reviewRepository.save(review);
        }
        return null;
    }

    public MovieResponse getTopRatedMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> movies = movieRepository.findTopRatedMovies(pageable);

        List<String> distinctGenres = movieRepository.findDistinctGenres();
        List<String> distinctLanguages = movieRepository.findDistinctLanguages();

        return new MovieResponse(movies.getContent(), distinctGenres, distinctLanguages, movies.getTotalPages());
    }

    public Movie updateMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public void deleteMovie(Long movieId) {
        movieRepository.deleteById(movieId);
    }

    public MovieResponse searchMovies(String title, int page, int itemsPerPage, String language, String genre, String releaseDate, String rating) {
        Pageable pageable = PageRequest.of(page, itemsPerPage);
        Page<Movie> result;

        if (title == null || title.trim().isEmpty()) {
            result = movieRepository.findAll(pageable);
        } else {
            result = movieRepository.findByTitleContainingIgnoreCase(title.trim(), pageable);
        }

        // Apply additional filters if provided
        List<Movie> filteredMovies = result.getContent();

        if (language != null && !language.isEmpty()) {
            filteredMovies = filteredMovies.stream()
                    .filter(movie -> movie.getLanguage().equalsIgnoreCase(language))
                    .collect(Collectors.toList());
        }

        if (genre != null && !genre.isEmpty()) {
            filteredMovies = filteredMovies.stream()
                    .filter(movie -> movie.getGenre().equalsIgnoreCase(genre))
                    .collect(Collectors.toList());
        }

        if (releaseDate != null && !releaseDate.isEmpty()) {
            filteredMovies = filteredMovies.stream()
                    .filter(movie -> movie.getReleaseDate().equals(releaseDate))
                    .collect(Collectors.toList());
        }

        // Assuming rating is a numeric value; adjust accordingly if necessary
        if (rating != null && !rating.isEmpty()) {
            double ratingValue = Double.parseDouble(rating);
            filteredMovies = filteredMovies.stream()
                    .filter(movie -> movie.getReviews() != null && movie.getReviews().stream()
                            .anyMatch(review -> review.getStarCount() >= ratingValue))
                    .collect(Collectors.toList());
        }

        List<String> distinctGenres = movieRepository.findDistinctGenres();
        List<String> distinctLanguages = movieRepository.findDistinctLanguages();

        int totalPages = (int) Math.ceil((double) filteredMovies.size() / itemsPerPage);

        return new MovieResponse(filteredMovies, distinctGenres, distinctLanguages, totalPages);
    }

}
