package com.demo.student.services;

import com.demo.student.dtos.StudentDTO;
import com.demo.student.entities.Student;
import com.demo.student.repositories.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<StudentDTO> getStudentById(Long id) {
        return studentRepository.findById(id).map(this::toDTO);
    }

    public Optional<StudentDTO> getStudentByCnie(String cnie) {
        return studentRepository.findByCnie(cnie).map(this::toDTO);
    }

    public StudentDTO createStudent(Student student) {
        if (studentRepository.findByCnie(student.getCnie()).isPresent()) {
            throw new IllegalArgumentException("A student with CNIE '" + student.getCnie() + "' already exists.");
        }
        return toDTO(studentRepository.save(student));
    }

    public Optional<StudentDTO> updateStudent(Long id, Student updated) {
        return studentRepository.findById(id).map(existing -> {
            existing.setCnie(updated.getCnie());
            existing.setFirstName(updated.getFirstName());
            existing.setLastName(updated.getLastName());
            existing.setEmail(updated.getEmail());
            return toDTO(studentRepository.save(existing));
        });
    }

    public boolean deleteStudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private StudentDTO toDTO(Student s) {
        return new StudentDTO(s.getId(), s.getCnie(), s.getFirstName(), s.getLastName(), s.getEmail());
    }
}
