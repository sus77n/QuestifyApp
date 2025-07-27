package com.example.iquiz.service;

import com.example.iquiz.dto.UserDto;
import com.example.iquiz.entity.User;
import com.example.iquiz.mapper.UserMapper;
import com.example.iquiz.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with the id: " + id));

        if (userDto.email() != null) {
            user.setEmail(userDto.email());
        }

        if (userDto.firstName() != null) {
            user.setFirstName(userDto.firstName());
        }

        if (userDto.lastName() != null) {
            user.setLastName(userDto.lastName());
        }

        if (userDto.role() != null) {
            user.setRole(userDto.role());
        }

        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
