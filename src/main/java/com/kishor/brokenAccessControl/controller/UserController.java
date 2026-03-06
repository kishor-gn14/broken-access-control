package com.kishor.brokenAccessControl.controller;

import com.kishor.brokenAccessControl.model.User;
import com.kishor.brokenAccessControl.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final Set<String> accessLog = new HashSet<>();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Username is required"
            ));
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Email is required"
            ));
        }

        if (user.getRole() == null || user.getRole().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Role is required"
            ));
        }

        if (userService.usernameExists(user.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Username '" + user.getUsername() + "' is already taken"
            ));
        }

        userService.registerUser(user);

        return ResponseEntity.status(201).body(Map.of(
                "message", "User registered successfully"
        ));
    }

    @GetMapping("/unsecured/{id}")
    public ResponseEntity<?> getUserUnsecured(
            @RequestHeader("userId") Long callerId,
            @PathVariable Long id) {

        Optional<User> caller = userService.findById(callerId);
        if (caller.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of(
                    "message", "No user found for userId: " + callerId
                            + ". Please register first via POST /api/users/register"
            ));
        }

        Optional<User> requestedUser = userService.findById(id);
        if (requestedUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                    "message", "No user profile found for id: " + id
            ));
        }

        if (id.equals(callerId)) {
            return ResponseEntity.ok(Map.of(
                    "profile", requestedUser.get(),
                    "message", "Access is allowed."
            ));
        }

        String accessKey = callerId + ":" + id;

        if (!accessLog.contains(accessKey)) {
            accessLog.add(accessKey);

            return ResponseEntity.ok(Map.of(
                    "profile", requestedUser.get(),
                    "message", "Unsecured. Accessing different profile. "
                            + "Further attempts will be blocked."
            ));
        }

        return ResponseEntity.status(403).body(Map.of(
                "message", "Access denied. Accessing different profile. "
        ));
    }

    @GetMapping("/secured/{id}")
    public ResponseEntity<?> getUserSecured(
            @RequestHeader("userId") Long callerId,
            @PathVariable Long id) {

        Optional<User> caller = userService.findById(callerId);
        if (caller.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of(
                    "message", "No user found for userId: " + callerId
                            + ". Please register first via POST /api/users/register"
            ));
        }

        Optional<User> requestedUser = userService.findById(id);
        if (requestedUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                    "message", "No user profile found for id: " + id
            ));
        }

        if (!id.equals(callerId)) {
            return ResponseEntity.status(403).body(Map.of(
                    "message", "Access denied. You can't access different profiles."
            ));
        }

        return ResponseEntity.ok(Map.of(
                "profile", requestedUser.get(),
                "message", "Access is allowed."
        ));
    }
}