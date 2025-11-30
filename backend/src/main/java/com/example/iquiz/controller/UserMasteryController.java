package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.entity.UserMastery;
import com.example.iquiz.entity.UserMasteryId;
import com.example.iquiz.service.UserMasteryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-mastery")
@RequiredArgsConstructor
public class UserMasteryController {

    private final UserMasteryService service;

    @PostMapping
    public ApiResponse<UserMastery> create(@RequestBody UserMastery mastery) {
        return ApiResponse.success(service.save(mastery), "UserMastery created successfully");
    }

    @GetMapping("/{userId}/{lessonId}/{exerciseTypeId}")
    public ApiResponse<UserMastery> getById(@PathVariable UUID userId,
                                            @PathVariable UUID lessonId,
                                            @PathVariable UUID exerciseTypeId) {
        UserMasteryId id = new UserMasteryId(userId, lessonId, exerciseTypeId);
        return ApiResponse.success(service.findById(id), "UserMastery retrieved successfully");
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<UserMastery>> getByUser(@PathVariable UUID userId) {
        return ApiResponse.success(service.findByUser(userId), "UserMasteries retrieved successfully");
    }

    @PutMapping("/update")
    public ApiResponse<UserMastery> updateMastery(@RequestParam UUID userId,
                                                  @RequestParam UUID lessonId,
                                                  @RequestParam UUID exerciseTypeId,
                                                  @RequestParam boolean correct) {
        return ApiResponse.success(service.updateMastery(userId, lessonId, exerciseTypeId, correct), "UserMastery updated successfully");
    }

    @DeleteMapping("/{userId}/{lessonId}/{exerciseTypeId}")
    public ApiResponse<Void> delete(@PathVariable UUID userId,
                                    @PathVariable UUID lessonId,
                                    @PathVariable UUID exerciseTypeId) {
        UserMasteryId id = new UserMasteryId(userId, lessonId, exerciseTypeId);
        service.delete(id);
        return ApiResponse.success(null, "User has been deleted successfully");
    }
}
