package com.moviereview.backend.controller;

import com.moviereview.backend.model.Movie;
import com.moviereview.backend.model.User;
import com.moviereview.backend.model.Review;
import com.moviereview.backend.service.MovieService;
import com.moviereview.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.moviereview.backend.dto.MovieResponse;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private UserService userService;

    @PostMapping("/{userId}")
    public Movie addMovie(@PathVariable Long userId, @RequestBody Movie movie) {
        User user = userService.findById(userId).orElse(null);
        if (user != null) {
            movie.setUser(user);
            return movieService.createMovie(movie);
        }
        return null;
    }

    @GetMapping("/user/{userId}")
    public List<Movie> getMoviesByUserId(@PathVariable Long userId) {
        return movieService.getMoviesByUserId(userId);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<Movie> getMovie(@PathVariable Long movieId) {
        Optional<Movie> movie = movieService.getMovieById(movieId);
        return movie.map(ResponseEntity::ok) // If found, return 200 OK with movie
                .orElseGet(() -> ResponseEntity.notFound().build()); // If not found, return 404 Not Found
    }

    @PostMapping("/{movieId}/reviews")
    public ResponseEntity<Review> addReview(@PathVariable Long movieId, @RequestBody Review review) {

        Review createdReview = movieService.addReview(movieId, review);
        if (createdReview != null) {
            return ResponseEntity.ok(createdReview); // Return the created review
        }
        return ResponseEntity.notFound().build(); // If movie not found
    }


    @PutMapping("/{movieId}")
    public ResponseEntity<Movie> updateMovie(
            @PathVariable Long movieId,
            @RequestBody Movie updatedMovie) {
        Optional<Movie> existingMovieOptional = movieService.getMovieById(movieId);

        if (existingMovieOptional.isPresent()) {
            Movie existingMovie = existingMovieOptional.get();
            // Update the fields of the movie with new values from the request body
            existingMovie.setTitle(updatedMovie.getTitle());
            existingMovie.setDescription(updatedMovie.getDescription());
            existingMovie.setGenre(updatedMovie.getGenre());
            existingMovie.setLanguage(updatedMovie.getLanguage());
            existingMovie.setReleaseDate(updatedMovie.getReleaseDate());
            existingMovie.setOttPlatform(updatedMovie.getOttPlatform());

            // Save the updated movie
            Movie updated = movieService.updateMovie(existingMovie);
            return ResponseEntity.ok(updated); // Return the updated movie
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if movie not found
        }
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long movieId) {
        Optional<Movie> movie = movieService.getMovieById(movieId);

        if (movie.isPresent()) {
            movieService.deleteMovie(movieId);  // Delete the movie
            return ResponseEntity.noContent().build(); // Return 204 No Content if deleted
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if movie not found
        }
    }


    @GetMapping("/top-rated")
    public MovieResponse getTopRatedMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return movieService.getTopRatedMovies(page, size);
    }

    @GetMapping("/search")
    public MovieResponse searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int itemsPerPage,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String releaseDate,
            @RequestParam(required = false) String rating) {
        return movieService.searchMovies(query, page, itemsPerPage, language, genre, releaseDate, rating);
    }







}