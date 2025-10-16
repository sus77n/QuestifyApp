package com.example.iquiz.controller;

import com.example.iquiz.entity.UserMastery;
import com.example.iquiz.entity.UserMasteryId;
import com.example.iquiz.service.UserMasteryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-mastery")
@RequiredArgsConstructor
public class UserMasteryController {

    private final UserMasteryService service;

    @PostMapping
    public ResponseEntity<UserMastery> create(@RequestBody UserMastery mastery) {
        return ResponseEntity.ok(service.save(mastery));
    }

    @GetMapping("/{userId}/{lessonId}/{exerciseTypeId}")
    public ResponseEntity<UserMastery> getById(@PathVariable Long userId,
                                               @PathVariable Long lessonId,
                                               @PathVariable Long exerciseTypeId) {
        UserMasteryId id = new UserMasteryId(userId, lessonId, exerciseTypeId);
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserMastery>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.findByUser(userId));
    }

    @PutMapping("/update")
    public ResponseEntity<UserMastery> updateMastery(@RequestParam Long userId,
                                                     @RequestParam Long lessonId,
                                                     @RequestParam Long exerciseTypeId,
                                                     @RequestParam boolean correct) {
        return ResponseEntity.ok(service.updateMastery(userId, lessonId, exerciseTypeId, correct));
    }

    @DeleteMapping("/{userId}/{lessonId}/{exerciseTypeId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId,
                                       @PathVariable Long lessonId,
                                       @PathVariable Long exerciseTypeId) {
        UserMasteryId id = new UserMasteryId(userId, lessonId, exerciseTypeId);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
