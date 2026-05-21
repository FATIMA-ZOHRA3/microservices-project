package com.demo.enrollment.clients;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;

/**
 * Typed client for the Student Service.
 * Returns a lightweight inner record to avoid raw Map usage.
 */
@Component
public class StudentClient {

    public record StudentInfo(Long id, String cnie, String firstName, String lastName, String email) {}

    private final WebClient.Builder webClientBuilder;

    public StudentClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Checks that a student with the given CNIE exists.
     * Returns the student info, or empty if not found.
     */
    public Optional<StudentInfo> getStudentByCnie(String cnie) {
        try {
            StudentInfo student = webClientBuilder.build()
                    .get()
                    .uri("http://student-service/api/students/cnie/" + cnie)
                    .retrieve()
                    .bodyToMono(StudentInfo.class)
                    .block();
            return Optional.ofNullable(student);
        } catch (WebClientResponseException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Cannot reach student-service: " + e.getMessage(), e);
        }
    }
}
