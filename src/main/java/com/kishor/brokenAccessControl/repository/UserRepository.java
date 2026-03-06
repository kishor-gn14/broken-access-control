package com.kishor.brokenAccessControl.repository;

import com.kishor.brokenAccessControl.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used during registration to prevent duplicate usernames
    boolean existsByUsername(String username);

    // Used to look up a user by username (future login use)
    Optional<User> findByUsername(String username);
}