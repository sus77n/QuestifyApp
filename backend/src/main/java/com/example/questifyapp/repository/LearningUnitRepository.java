package com.example.questifyapp.repository;

import com.example.questifyapp.entity.LearningUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningUnitRepository extends JpaRepository<LearningUnit, Long> {

    // Find learning units by title
    List<LearningUnit> findByTitleContainingIgnoreCase(String title);

    // Find learning units by type
    List<LearningUnit> findByType(String type);

    // Find learning units by status
    List<LearningUnit> findByStatus(int status);

    // Find learning units by level
    List<LearningUnit> findByLevel(int level);

    // Find all root learning units (units with no parent)
    List<LearningUnit> findByParentUnitIsNull();

    // Find all child units of a specific parent
    List<LearningUnit> findByParentUnitId(Long parentId);

    // Find learning units by parent unit
    List<LearningUnit> findByParentUnit(LearningUnit parentUnit);

    // Find learning units by level and type
    List<LearningUnit> findByLevelAndType(int level, String type);

    // Find learning units by status and type
    List<LearningUnit> findByStatusAndType(int status, String type);

    // Custom query to find learning units by title search
    @Query("SELECT lu FROM LearningUnit lu WHERE LOWER(lu.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(lu.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<LearningUnit> searchByTitleOrDescription(@Param("searchTerm") String searchTerm);

    // Find learning units ordered by level
    List<LearningUnit> findAllByOrderByLevelAsc();

    // Check if learning unit exists by title
    boolean existsByTitle(String title);

    // Find learning units by level range
    @Query("SELECT lu FROM LearningUnit lu WHERE lu.level BETWEEN :minLevel AND :maxLevel")
    List<LearningUnit> findByLevelRange(@Param("minLevel") int minLevel, @Param("maxLevel") int maxLevel);
}
