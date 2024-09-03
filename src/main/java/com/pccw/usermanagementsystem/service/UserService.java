package com.pccw.usermanagementsystem.service;


import com.pccw.usermanagementsystem.service.email.EmailService;
import com.pccw.usermanagementsystem.entity.User;
import com.pccw.usermanagementsystem.exception.*;
import com.pccw.usermanagementsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    @Transactional
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException(user.getUsername());
        }
        User savedUser = userRepository.save(user);

        emailService.sendWelcomeEmail(savedUser.getUsername(), "Welcome!", "Thank you for registering!");

        return savedUser;

    }

    @Transactional
    public List<User> registerUsers(List<User> users) {
        users.forEach(user -> user.setPassword(passwordEncoder.encode(user.getPassword())));

        List<String> usernames = users.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());

        List<String> existingUsernames = findExistingUsernames(usernames);

        if (!existingUsernames.isEmpty()) {
            throw new UserAlreadyExistsException(String.join(", ", existingUsernames));
        }

        List<User> savedUsers = userRepository.saveAll(users);

        savedUsers.forEach(user -> emailService.sendWelcomeEmail(user.getUsername(), "Welcome!", "Thank you for registering!"));

        return savedUsers;
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<String> findExistingUsernames(List<String> usernames) {
        return userRepository.findByUsernameIn(usernames).stream().map(User::getUsername).collect(Collectors.toList());
    }

    public User findActiveUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(User toBeUpdated) {
        User user = userRepository.findById(toBeUpdated.getId()).orElseThrow(() -> new UserNotFoundException(toBeUpdated.getId()));

        user.setUsername(toBeUpdated.getUsername());
        user.setPassword(passwordEncoder.encode(toBeUpdated.getPassword()));
        user.setRole(toBeUpdated.getRole());
        user.setEnabled(toBeUpdated.getEnabled());
        return userRepository.save(user);
    }

    @Transactional
    public List<User> updateUsers(List<User> toBeUpdated) {
        List<Long> toBeUpdatedIds = toBeUpdated.stream().map(User::getId).collect(Collectors.toList());

        List<User> foundUsers = userRepository.findAllById(toBeUpdatedIds);

        // Check if there are any missing IDs and throw an exception for the first missing id found
        validateAllUsersFound(toBeUpdatedIds, foundUsers.stream().map(User::getId).toList());

        foundUsers.forEach(user -> {
            User userDetails = toBeUpdated.stream().filter(details -> details.getId().equals(user.getId())).findFirst().get();

            user.setUsername(userDetails.getUsername());
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            user.setRole(userDetails.getRole());
            user.setEnabled(userDetails.getEnabled());
        });

        return userRepository.saveAll(foundUsers);
    }


    @Transactional
    public void softDeleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        user.setEnabled(false);
        userRepository.save(user);
    }


    @Transactional
    public void softDeleteUsers(List<Long> toBeDeletedIds) {
        List<User> foundUsers = userRepository.findAllById(toBeDeletedIds);

        validateAllUsersFound(toBeDeletedIds, foundUsers.stream().map(User::getId).toList());

        foundUsers.forEach(user -> user.setEnabled(false));
        userRepository.saveAll(foundUsers);
    }

    private void validateAllUsersFound(List<Long> toBeUpdatedIds, List<Long> foundIds) {
        if (foundIds.size() != toBeUpdatedIds.size()) {

            Long missingId = toBeUpdatedIds.stream().filter(id -> !foundIds.contains(id)).findFirst().get();

            throw new UserNotFoundException(missingId);
        }
    }
}
