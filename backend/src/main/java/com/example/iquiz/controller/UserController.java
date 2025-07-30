package com.example.iquiz.controller;

import com.example.iquiz.dto.UserDto;
import com.example.iquiz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> editUser(@PathVariable Long id,@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }
}
