package com.demo.course.services;

import com.demo.course.dtos.CourseDTO;
import com.demo.course.entities.Course;
import com.demo.course.repositories.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<CourseDTO> getCourseById(Long id) {
        return courseRepository.findById(id).map(this::toDTO);
    }

    public CourseDTO createCourse(Course course) {
        return toDTO(courseRepository.save(course));
    }

    public Optional<CourseDTO> updateCourse(Long id, Course updated) {
        return courseRepository.findById(id).map(existing -> {
            existing.setTitle(updated.getTitle());
            existing.setDescription(updated.getDescription());
            existing.setCredits(updated.getCredits());
            return toDTO(courseRepository.save(existing));
        });
    }

    public boolean deleteCourse(Long id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private CourseDTO toDTO(Course c) {
        return new CourseDTO(c.getId(), c.getTitle(), c.getDescription(), c.getCredits());
    }
}
