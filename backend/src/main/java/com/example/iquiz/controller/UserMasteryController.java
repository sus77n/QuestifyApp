package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.entity.UserMastery;
import com.example.iquiz.entity.UserMasteryId;
import com.example.iquiz.service.UserMasteryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ApiResponse<UserMastery> getById(@PathVariable Long userId,
                                            @PathVariable Long lessonId,
                                            @PathVariable Long exerciseTypeId) {
        UserMasteryId id = new UserMasteryId(userId, lessonId, exerciseTypeId);
        return ApiResponse.success(service.findById(id), "UserMastery retrieved successfully");
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<UserMastery>> getByUser(@PathVariable Long userId) {
        return ApiResponse.success(service.findByUser(userId), "UserMasteries retrieved successfully");
    }

    @PutMapping("/update")
    public ApiResponse<UserMastery> updateMastery(@RequestParam Long userId,
                                                  @RequestParam Long lessonId,
                                                  @RequestParam Long exerciseTypeId,
                                                  @RequestParam boolean correct) {
        return ApiResponse.success(service.updateMastery(userId, lessonId, exerciseTypeId, correct), "UserMastery updated successfully");
    }

    @DeleteMapping("/{userId}/{lessonId}/{exerciseTypeId}")
    public ApiResponse<Void> delete(@PathVariable Long userId,
                                    @PathVariable Long lessonId,
                                    @PathVariable Long exerciseTypeId) {
        UserMasteryId id = new UserMasteryId(userId, lessonId, exerciseTypeId);
        service.delete(id);
        return ApiResponse.success(null, "User has been deleted successfully");
    }
}
