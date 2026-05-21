package com.demo.enrollment.clients;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;

/**
 * Typed client for the Course Service.
 */
@Component
public class CourseClient {

    public record CourseInfo(Long id, String title, String description, int credits) {}

    private final WebClient.Builder webClientBuilder;

    public CourseClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Fetches course info by ID.
     * Returns empty if the course does not exist.
     */
    public Optional<CourseInfo> getCourseById(Long courseId) {
        try {
            CourseInfo course = webClientBuilder.build()
                    .get()
                    .uri("http://course-service/api/courses/" + courseId)
                    .retrieve()
                    .bodyToMono(CourseInfo.class)
                    .block();
            return Optional.ofNullable(course);
        } catch (WebClientResponseException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Cannot reach course-service: " + e.getMessage(), e);
        }
    }
}
