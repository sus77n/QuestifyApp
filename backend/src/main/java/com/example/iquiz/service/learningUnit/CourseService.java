package com.example.iquiz.service.learningUnit;

import com.example.iquiz.dto.learningUnit.CourseDto;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.entity.LearningUnitType;
import com.example.iquiz.entity.User;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.mapper.LearningUnitMapper;
import com.example.iquiz.repository.LearningUnitRepository;
import com.example.iquiz.repository.LearningUnitTypeRepository;
import com.example.iquiz.utility.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CourseService {
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private LearningUnitTypeRepository learningUnitTypeRepository;
    @Autowired
    private LearningUnitMapper learningUnitMapper;
    @Autowired
    private LearningUnitRepository learningUnitRepository;

    public List<CourseDto> getAllCoursesWithAuth() {
        User user = userUtil.getUserFromAuthContext();
        LearningUnitType type = learningUnitTypeRepository.findByName("Course")
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit Type", "name", "Course"));

        List<CourseDto> courseDtos = null;
        if (user.getRole().name().toString().equals("ADMIN")) {
            List<LearningUnit> learningUnits = learningUnitRepository.findAllByType_Name("Course");
            courseDtos = learningUnits.stream().map(learningUnitMapper::toCourseDto).toList();
        } else {
            List<LearningUnit> learningUnits = learningUnitRepository.findByType_IdAndCreatedBy_Id(type.getId(), user.getId());
            courseDtos = learningUnits.stream().map(learningUnitMapper::toCourseDto).toList();
        }

        return courseDtos;
    }

    public CourseDto saveCourse(CourseDto dto) {
        User user = userUtil.getUserFromAuthContext();
        LearningUnitType type = learningUnitTypeRepository.findByName("Course")
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit Type", "name", "Course"));

        LearningUnit entity = learningUnitMapper.courseDtoToEntity(dto);
        entity.setCreatedBy(user);
        entity.setType(type);
        entity = learningUnitRepository.save(entity);
        return learningUnitMapper.toCourseDto(entity);
    }

    public CourseDto updateCourse(UUID id, CourseDto dto) {
        LearningUnit learningUnit = learningUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        learningUnit.setName(dto.name());
        learningUnit.setCode(dto.code());
        learningUnit.setDescription(dto.description());

        learningUnit = learningUnitRepository.save(learningUnit);
        return learningUnitMapper.toCourseDto(learningUnit);
    }

    public void deleteCourse(UUID courseId) {
        LearningUnit learningUnit = learningUnitRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));
        learningUnitRepository.delete(learningUnit);
    }

}
