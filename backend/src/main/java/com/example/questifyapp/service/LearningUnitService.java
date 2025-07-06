package com.example.questifyapp.service;

import com.example.questifyapp.entity.LearningUnit;
import com.example.questifyapp.repository.LearningUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LearningUnitService {
    
    @Autowired
    private LearningUnitRepository learningUnitRepository;

    // Basic CRUD Operations
    
    /**
     * Get all learning units
     */
    public List<LearningUnit> getAllLearningUnits() {
        return learningUnitRepository.findAll();
    }

    /**
     * Get learning unit by ID
     */
    public LearningUnit getLearningUnitById(Long id) {
        return learningUnitRepository.findById(id).orElse(null);
    }

    /**
     * Get learning unit by ID with Optional return
     */
    public Optional<LearningUnit> findLearningUnitById(Long id) {
        return learningUnitRepository.findById(id);
    }

    /**
     * Create a new learning unit
     */
    public LearningUnit createLearningUnit(LearningUnit learningUnit) {
        return learningUnitRepository.save(learningUnit);
    }

    /**
     * Update an existing learning unit
     */
    public LearningUnit updateLearningUnit(LearningUnit learningUnit) {
        return learningUnitRepository.save(learningUnit);
    }

    /**
     * Delete learning unit by ID
     */
    public void deleteLearningUnit(Long id) {
        learningUnitRepository.deleteById(id);
    }

    /**
     * Check if learning unit exists by ID
     */
    public boolean existsById(Long id) {
        return learningUnitRepository.existsById(id);
    }

    // Additional business logic methods based on your repository
    
    /**
     * Search learning units by title
     */
    public List<LearningUnit> searchByTitle(String title) {
        return learningUnitRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Search learning units by title or description
     */
    public List<LearningUnit> searchByTitleOrDescription(String searchTerm) {
        return learningUnitRepository.searchByTitleOrDescription(searchTerm);
    }

    /**
     * Get learning units by type
     */
    public List<LearningUnit> getLearningUnitsByType(String type) {
        return learningUnitRepository.findByType(type);
    }

    /**
     * Get learning units by status
     */
    public List<LearningUnit> getLearningUnitsByStatus(int status) {
        return learningUnitRepository.findByStatus(status);
    }

    /**
     * Get learning units by level
     */
    public List<LearningUnit> getLearningUnitsByLevel(int level) {
        return learningUnitRepository.findByLevel(level);
    }

    /**
     * Get all root learning units (units with no parent)
     */
    public List<LearningUnit> getRootLearningUnits() {
        return learningUnitRepository.findByParentUnitIsNull();
    }

    /**
     * Get all child units of a specific parent
     */
    public List<LearningUnit> getChildUnits(Long parentId) {
        return learningUnitRepository.findByParentUnitId(parentId);
    }

    /**
     * Get child units by parent unit
     */
    public List<LearningUnit> getChildUnits(LearningUnit parentUnit) {
        return learningUnitRepository.findByParentUnit(parentUnit);
    }

    /**
     * Get learning units by level and type
     */
    public List<LearningUnit> getLearningUnitsByLevelAndType(int level, String type) {
        return learningUnitRepository.findByLevelAndType(level, type);
    }

    /**
     * Get learning units by status and type
     */
    public List<LearningUnit> getLearningUnitsByStatusAndType(int status, String type) {
        return learningUnitRepository.findByStatusAndType(status, type);
    }

    /**
     * Get all learning units ordered by level
     */
    public List<LearningUnit> getAllLearningUnitsOrderedByLevel() {
        return learningUnitRepository.findAllByOrderByLevelAsc();
    }

    /**
     * Check if learning unit exists by title
     */
    public boolean existsByTitle(String title) {
        return learningUnitRepository.existsByTitle(title);
    }

    /**
     * Get learning units by level range
     */
    public List<LearningUnit> getLearningUnitsByLevelRange(int minLevel, int maxLevel) {
        return learningUnitRepository.findByLevelRange(minLevel, maxLevel);
    }

    // Business logic methods for hierarchical operations

    /**
     * Add a child unit to a parent unit
     */
    public LearningUnit addChildUnit(Long parentId, LearningUnit childUnit) {
        LearningUnit parentUnit = getLearningUnitById(parentId);
        if (parentUnit != null) {
            childUnit.setParentUnit(parentUnit);
            return learningUnitRepository.save(childUnit);
        }
        return null;
    }

    /**
     * Remove a child unit from its parent
     */
    public LearningUnit removeFromParent(Long childId) {
        LearningUnit childUnit = getLearningUnitById(childId);
        if (childUnit != null) {
            childUnit.setParentUnit(null);
            return learningUnitRepository.save(childUnit);
        }
        return null;
    }

    /**
     * Count total learning units
     */
    public long countLearningUnits() {
        return learningUnitRepository.count();
    }

    /**
     * Count learning units by type
     */
    public long countLearningUnitsByType(String type) {
        return learningUnitRepository.findByType(type).size();
    }

    /**
     * Count learning units by status
     */
    public long countLearningUnitsByStatus(int status) {
        return learningUnitRepository.findByStatus(status).size();
    }
}
