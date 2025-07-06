package com.example.questifyapp.controller;

import com.example.questifyapp.dto.LearningUnitDto;
import com.example.questifyapp.entity.LearningUnit;
import com.example.questifyapp.service.LearningUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/learning-units")
public class LearningUnitController {
    
    @Autowired
    private LearningUnitService learningUnitService;

    // Basic CRUD Operations

    /**
     * Get all learning units
     */
    @GetMapping("")
    public ResponseEntity<List<LearningUnitDto>> getAllLearningUnits() {
        List<LearningUnit> learningUnits = learningUnitService.getAllLearningUnits();
        return ResponseEntity.ok(learningUnits.stream()
                .map(LearningUnitDto::fromEntity)
                .toList());
    }

    /**
     * Get learning unit by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<LearningUnitDto> getLearningUnitById(@PathVariable Long id) {
        LearningUnit learningUnit = learningUnitService.getLearningUnitById(id);
        if (learningUnit == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(LearningUnitDto.fromEntity(learningUnit));
    }

    /**
     * Create a new learning unit
     */
    @PostMapping("")
    public ResponseEntity<LearningUnitDto> createLearningUnit(@RequestBody LearningUnit learningUnit) {
        try {
            LearningUnit createdUnit = learningUnitService.createLearningUnit(learningUnit);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(LearningUnitDto.fromEntity(createdUnit));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Update an existing learning unit
     */
    @PutMapping("/{id}")
    public ResponseEntity<LearningUnitDto> updateLearningUnit(
            @PathVariable Long id, 
            @RequestBody LearningUnit learningUnit) {
        
        if (!learningUnitService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        learningUnit.setId(id);
        try {
            LearningUnit updatedUnit = learningUnitService.updateLearningUnit(learningUnit);
            return ResponseEntity.ok(LearningUnitDto.fromEntity(updatedUnit));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Delete learning unit by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteLearningUnit(@PathVariable Long id) {
        if (!learningUnitService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            learningUnitService.deleteLearningUnit(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Learning unit deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to delete learning unit");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Search and Filter Operations

    /**
     * Search learning units by title
     */
    @GetMapping("/search/title")
    public ResponseEntity<List<LearningUnitDto>> searchByTitle(@RequestParam String title) {
        List<LearningUnit> learningUnits = learningUnitService.searchByTitle(title);
        if (learningUnits.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(learningUnits.stream()
                .map(LearningUnitDto::fromEntity)
                .toList());
    }

    /**
     * Search learning units by title or description
     */
    @GetMapping("/search")
    public ResponseEntity<List<LearningUnitDto>> search(@RequestParam String searchTerm) {
        List<LearningUnit> learningUnits = learningUnitService.searchByTitleOrDescription(searchTerm);
        if (learningUnits.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(learningUnits.stream()
                .map(LearningUnitDto::fromEntity)
                .toList());
    }

    /**
     * Get learning units by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<LearningUnitDto>> getLearningUnitsByType(@PathVariable String type) {
        List<LearningUnit> learningUnits = learningUnitService.getLearningUnitsByType(type);
        return ResponseEntity.ok(learningUnits.stream()
                .map(LearningUnitDto::fromEntity)
                .toList());
    }

    /**
     * Get learning units by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LearningUnitDto>> getLearningUnitsByStatus(@PathVariable int status) {
        List<LearningUnit> learningUnits = learningUnitService.getLearningUnitsByStatus(status);
        return ResponseEntity.ok(learningUnits.stream()
                .map(LearningUnitDto::fromEntity)
                .toList());
    }

    /**
     * Get learning units by level
     */
    @GetMapping("/level/{level}")
    public ResponseEntity<List<LearningUnitDto>> getLearningUnitsByLevel(@PathVariable int level) {
        List<LearningUnit> learningUnits = learningUnitService.getLearningUnitsByLevel(level);
        return ResponseEntity.ok(learningUnits.stream()
                .map(LearningUnitDto::fromEntity)
                .toList());
    }

    // Hierarchical Operations

    /**
     * Get all root learning units (units with no parent)
     */
    @GetMapping("/root")
    public ResponseEntity<List<LearningUnitDto>> getRootLearningUnits() {
        List<LearningUnit> learningUnits = learningUnitService.getRootLearningUnits();
        return ResponseEntity.ok(learningUnits.stream()
                .map(LearningUnitDto::fromEntity)
                .toList());
    }

    /**
     * Get all child units of a specific parent
     */
    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<LearningUnitDto>> getChildUnits(@PathVariable Long parentId) {
        List<LearningUnit> childUnits = learningUnitService.getChildUnits(parentId);
        return ResponseEntity.ok(childUnits.stream()
                .map(LearningUnitDto::fromEntity)
                .toList());
    }

    /**
     * Add a child unit to a parent unit
     */
    @PostMapping("/{parentId}/children")
    public ResponseEntity<LearningUnitDto> addChildUnit(
            @PathVariable Long parentId, 
            @RequestBody LearningUnit childUnit) {
        
        if (!learningUnitService.existsById(parentId)) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            LearningUnit createdChild = learningUnitService.addChildUnit(parentId, childUnit);
            if (createdChild != null) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(LearningUnitDto.fromEntity(createdChild));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Remove a child unit from its parent
     */
    @PutMapping("/{childId}/remove-parent")
    public ResponseEntity<LearningUnitDto> removeFromParent(@PathVariable Long childId) {
        if (!learningUnitService.existsById(childId)) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            LearningUnit updatedChild = learningUnitService.removeFromParent(childId);
            if (updatedChild != null) {
                return ResponseEntity.ok(LearningUnitDto.fromEntity(updatedChild));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Utility Operations

    /**
     * Get all learning units ordered by level
     */
    @GetMapping("/ordered-by-level")
    public ResponseEntity<List<LearningUnitDto>> getAllLearningUnitsOrderedByLevel() {
        List<LearningUnit> learningUnits = learningUnitService.getAllLearningUnitsOrderedByLevel();
        return ResponseEntity.ok(learningUnits.stream()
                .map(LearningUnitDto::fromEntity)
                .toList());
    }

    /**
     * Get learning units by level range
     */
    @GetMapping("/level-range")
    public ResponseEntity<List<LearningUnitDto>> getLearningUnitsByLevelRange(
            @RequestParam int minLevel, 
            @RequestParam int maxLevel) {
        
        List<LearningUnit> learningUnits = learningUnitService.getLearningUnitsByLevelRange(minLevel, maxLevel);
        return ResponseEntity.ok(learningUnits.stream()
                .map(LearningUnitDto::fromEntity)
                .toList());
    }

    /**
     * Get learning units by level and type
     */
    @GetMapping("/filter")
    public ResponseEntity<List<LearningUnitDto>> getLearningUnitsByLevelAndType(
            @RequestParam int level, 
            @RequestParam String type) {
        
        List<LearningUnit> learningUnits = learningUnitService.getLearningUnitsByLevelAndType(level, type);
        return ResponseEntity.ok(learningUnits.stream()
                .map(LearningUnitDto::fromEntity)
                .toList());
    }

    // Statistics Operations

    /**
     * Get total count of learning units
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalCount() {
        long count = learningUnitService.countLearningUnits();
        Map<String, Long> response = new HashMap<>();
        response.put("total", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get count of learning units by type
     */
    @GetMapping("/count/type/{type}")
    public ResponseEntity<Map<String, Long>> getCountByType(@PathVariable String type) {
        long count = learningUnitService.countLearningUnitsByType(type);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        response.put("type", (long) type.hashCode()); // Just for response structure
        return ResponseEntity.ok(response);
    }

    /**
     * Get count of learning units by status
     */
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Map<String, Long>> getCountByStatus(@PathVariable int status) {
        long count = learningUnitService.countLearningUnitsByStatus(status);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        response.put("status", (long) status);
        return ResponseEntity.ok(response);
    }

    /**
     * Check if learning unit exists by title
     */
    @GetMapping("/exists/title/{title}")
    public ResponseEntity<Map<String, Boolean>> existsByTitle(@PathVariable String title) {
        boolean exists = learningUnitService.existsByTitle(title);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}
