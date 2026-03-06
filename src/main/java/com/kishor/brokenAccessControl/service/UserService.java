package com.kishor.brokenAccessControl.service;

import com.kishor.brokenAccessControl.model.User;

import java.util.Optional;

public interface UserService {

    boolean usernameExists(String username);
    User registerUser(User user);
    Optional<User> findById(Long id);
}