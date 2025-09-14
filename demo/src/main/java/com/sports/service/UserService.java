package com.sports.service;

import com.sports.entity.User;
import com.sports.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Password encoder for hashing passwords
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Formatter for yyyy-MM-dd
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // CREATE
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set created & updated dates
        String today = LocalDate.now().format(formatter);
        user.setCreatedAt(today);
        user.setUpdatedOn(today);

        return userRepository.save(user);
    }

    // READ - by ID
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    // READ - all
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // UPDATE
    public Optional<User> updateUser(String id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setFullname(updatedUser.getFullname());
            user.setEmail(updatedUser.getEmail());
            user.setDob(updatedUser.getDob());
            user.setGender(updatedUser.getGender());
            user.setLocation(updatedUser.getLocation());
            user.setContact(updatedUser.getContact());
            user.setRole(updatedUser.getRole());

            // Re-hash password if changed
            if (!passwordEncoder.matches(updatedUser.getPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            // update timestamp
            user.setUpdatedOn(LocalDate.now().format(formatter));

            return userRepository.save(user);
        });
    }

    // DELETE
    public boolean deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // FIND BY EMAIL - for SignIn
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // VERIFY PASSWORD - for SignIn
    public boolean verifyPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    // AUTHENTICATE USER
    public Optional<User> authenticate(String email, String rawPassword) {
        return getUserByEmail(email)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
    }

}
