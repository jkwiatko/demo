package com.jkwiatko.demo.user.controller;


import com.jkwiatko.demo.user.entity.User;
import com.jkwiatko.demo.user.model.request.UserRequest;
import com.jkwiatko.demo.user.model.response.UserResponse;
import com.jkwiatko.demo.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private ModelMapper modelMapper;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(user -> modelMapper.map(user, UserResponse.class)).toList();
    }

    @GetMapping("/me")
    public UserResponse getCurrentlyLoginUser(@AuthenticationPrincipal UserDetails currentUser) {
        return userRepository.findByEmail(currentUser.getUsername()).map(user -> modelMapper.map(user, UserResponse.class)).orElse(null);
    }

    @PostMapping
    public User addUser(@RequestBody @Valid UserRequest userRequest) {
        var user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        return userRepository.save(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@AuthenticationPrincipal UserDetails currentUser, @RequestBody @Valid UserRequest userRequest, @PathVariable Long id) {
        if (isCurrentUserIdDiffer(currentUser, id)) {
            var user = userRepository.getById(id);
            user.setEmail(userRequest.getEmail());
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("You cannot edit currently logged in user");
        }
    }

    @DeleteMapping("/{id}")
    public void removeUser(@AuthenticationPrincipal UserDetails currentUser, @PathVariable Long id) {
        if (isCurrentUserIdDiffer(currentUser, id)) {
            userRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("You cannot delete currently logged in user");
        }
    }

    private boolean isCurrentUserIdDiffer(UserDetails currentUser, Long id) {
        return userRepository.findByEmail(currentUser.getUsername()).map(User::getId).filter(id::equals).isEmpty();
    }
}
