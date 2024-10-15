package com.moviereview.backend.controller;

import com.moviereview.backend.model.User;
import com.moviereview.backend.service.PasswordService;
import com.moviereview.backend.service.UserService;
import com.moviereview.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap; // Import for HashMap
import java.util.Map; // Import for Map
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signupUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        // Check if the email already exists
        if (userService.getUserByEmail(user.getEmail()) != null) {
            response.put("success", false);
            response.put("message", "Email is already registered");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Encrypt the password
        String encryptedPassword = passwordService.encryptPassword(user.getPassword());
        user.setPassword(encryptedPassword); // Set the encrypted password
        userService.saveUser(user);

        // Generate JWT token using user ID
        String token = jwtUtil.generateToken(user.getId()); // Change to user.getId()

        // Return success response with token and message
        response.put("success", true);
        response.put("message", "User signed up successfully!");
        response.put("token", token);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signinUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        // Retrieve the user by email
        User existingUser = userService.getUserByEmail(user.getEmail());

        // Check if user exists and password is valid
        if (existingUser == null || !passwordService.verifyPassword(user.getPassword(), existingUser.getPassword())) {
            response.put("success", false);
            response.put("message", "Invalid email or password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Generate JWT token using user ID
        String token = jwtUtil.generateToken(existingUser.getId()); // Change to existingUser.getId()

        // Return success response with token and message
        response.put("success", true);
        response.put("message", "User signed in successfully!");
        response.put("token", token);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> getLoggedInUser(@RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();

        // Extract user ID from the token
        Long userId = jwtUtil.extractUserId(token.replace("Bearer ", "")); // Ensure this method extracts user ID
        User user = userService.findById(userId).orElse(null); // Get user details

        if (user == null) {
            response.put("success", false);
            response.put("message", "User not found");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Remove the password before sending user details
        user.setPassword(null); // Set password to null to avoid sending it to frontend

        // Return user details
        response.put("success", true);
        response.put("user", user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
