package com.demo.enrollment.services;

import com.demo.enrollment.clients.CourseClient;
import com.demo.enrollment.clients.CourseClient.CourseInfo;
import com.demo.enrollment.clients.StudentClient;
import com.demo.enrollment.dtos.EnrollmentRequest;
import com.demo.enrollment.dtos.EnrollmentResponseDTO;
import com.demo.enrollment.entities.Enrollment;
import com.demo.enrollment.repositories.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    private static final int MAX_STUDENTS_PER_COURSE = 3;
    private static final int CANCELLATION_WINDOW_HOURS = 24;

    private final EnrollmentRepository enrollmentRepository;
    private final StudentClient studentClient;
    private final CourseClient courseClient;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                              StudentClient studentClient,
                              CourseClient courseClient) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentClient = studentClient;
        this.courseClient = courseClient;
    }

    public List<EnrollmentResponseDTO> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        
        return enrollments.stream().map(enrollment -> {
            String courseName = courseClient.getCourseById(enrollment.getCourseId())
                    .map(CourseInfo::title)
                    .orElse("Unknown Course");
            return toDTO(enrollment, courseName);
        }).collect(Collectors.toList());
    }

    
    public EnrollmentResponseDTO enrollStudent(EnrollmentRequest request) {

        studentClient.getStudentByCnie(request.getCnie())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Student with CNIE '" + request.getCnie() + "' not found."));

        CourseInfo course = courseClient.getCourseById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Course with ID " + request.getCourseId() + " not found."));

        // 3. Check course capacity
        long currentCount = enrollmentRepository.countByCourseId(request.getCourseId());
        if (currentCount >= MAX_STUDENTS_PER_COURSE) {
            throw new IllegalStateException(
                    "Course '" + course.title() + "' is full (max " + MAX_STUDENTS_PER_COURSE + " students).");
        }

        if (enrollmentRepository.existsByStudentCnieAndCourseId(request.getCnie(), request.getCourseId())) {
            throw new IllegalStateException(
                    "Student '" + request.getCnie() + "' is already enrolled in '" + course.title() + "'.");
        }

        // 5. Save
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentCnie(request.getCnie());
        enrollment.setCourseId(request.getCourseId());
        enrollment.setEnrollmentDate(LocalDateTime.now());

        Enrollment saved = enrollmentRepository.save(enrollment);
        return toDTO(saved, course.title());
    }

     List<EnrollmentResponseDTO> getEnrollmentsByCnie(String cnie) {

        studentClient.getStudentByCnie(cnie)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Student with CNIE '" + cnie + "' not found."));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentCnie(cnie);

        return enrollments.stream().map(enrollment -> {
            String courseName = courseClient.getCourseById(enrollment.getCourseId())
                    .map(CourseInfo::title)
                    .orElse("Unknown Course");
            return toDTO(enrollment, courseName);
        }).collect(Collectors.toList());
    }
    
    
    public void cancelEnrollment(Long enrollmentId, String cnie) {

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Enrollment not found with ID: " + enrollmentId));

        if (!enrollment.getStudentCnie().equals(cnie)) {
            throw new IllegalArgumentException("This enrollment does not belong to student '" + cnie + "'.");
        }

        // Check 24-hour window
        LocalDateTime cutoff = LocalDateTime.now().minusHours(CANCELLATION_WINDOW_HOURS);
        if (enrollment.getEnrollmentDate().isBefore(cutoff)) {
            throw new IllegalStateException(
                    "Cancellation window expired. Enrollments can only be cancelled within 24 hours.");
        }

        enrollmentRepository.delete(enrollment);
    }


    private EnrollmentResponseDTO toDTO(Enrollment enrollment, String courseName) {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(CANCELLATION_WINDOW_HOURS);
        boolean deletable = enrollment.getEnrollmentDate().isAfter(cutoff);

        return new EnrollmentResponseDTO(
                enrollment.getId(),
                enrollment.getStudentCnie(),
                courseName,
                enrollment.getEnrollmentDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                deletable
        );
    }
}