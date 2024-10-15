package com.moviereview.backend.service;

import com.moviereview.backend.model.User;
import com.moviereview.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email); 
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
