package com.demo.enrollment.repositories;

import com.demo.enrollment.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentCnie(String studentCnie);

    long countByCourseId(Long courseId);

    boolean existsByStudentCnieAndCourseId(String studentCnie, Long courseId);
}
