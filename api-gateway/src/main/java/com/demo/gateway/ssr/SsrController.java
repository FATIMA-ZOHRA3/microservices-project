package com.demo.gateway.ssr;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Controller
public class SsrController {

    private final WebClient.Builder webClientBuilder;

    public SsrController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    private WebClient loadBalancedClient() {
        return webClientBuilder.build();
    }

    private Mono<List> fetchFromService(String serviceName, String path) {
        return loadBalancedClient()
                .get()
                .uri("lb://" + serviceName + path)
                .retrieve()
                .bodyToMono(List.class)
                .onErrorReturn(List.of());
    }

    // ── Routes SSR ──

    @GetMapping("/")
    public Mono<String> home(Model model) {
        Mono<List> studentsMono    = fetchFromService("student-service", "/api/students");
        Mono<List> coursesMono     = fetchFromService("course-service", "/api/courses");
        Mono<List> enrollmentsMono = fetchFromService("enrollment-service", "/api/enrollments");

        return Mono.zip(studentsMono, coursesMono, enrollmentsMono)
                .map(tuple -> {
                    List<?> students    = tuple.getT1();
                    List<?> courses     = tuple.getT2();
                    List<?> enrollments = tuple.getT3();

                    model.addAttribute("students",        students);
                    model.addAttribute("courses",         courses);
                    model.addAttribute("enrollments",     enrollments);
                    model.addAttribute("studentCount",    students.size());
                    model.addAttribute("courseCount",     courses.size());
                    model.addAttribute("enrollmentCount", enrollments.size());
                    model.addAttribute("studentsUp",      !students.isEmpty());
                    model.addAttribute("coursesUp",       !courses.isEmpty());
                    model.addAttribute("enrollsUp",       !enrollments.isEmpty());

                    return "index";  // ← CHANGÉ: "index" au lieu de "edugate"
                });
    }

    @GetMapping("/ssr/students")
    public Mono<String> ssrStudents(Model model) {
        return fetchFromService("student-service", "/api/students")
                .map(students -> {
                    model.addAttribute("students", students);
                    model.addAttribute("studentCount", students.size());
                    model.addAttribute("studentsUp", !students.isEmpty());
                    model.addAttribute("activeTab", "students");
                    return "index";  // ← CHANGÉ
                });
    }

    @GetMapping("/ssr/courses")
    public Mono<String> ssrCourses(Model model) {
        return fetchFromService("course-service", "/api/courses")
                .map(courses -> {
                    model.addAttribute("courses", courses);
                    model.addAttribute("courseCount", courses.size());
                    model.addAttribute("coursesUp", !courses.isEmpty());
                    model.addAttribute("activeTab", "courses");
                    return "index";  // ← CHANGÉ
                });
    }

    @GetMapping("/ssr/enroll")
    public Mono<String> ssrEnrollForm(Model model) {
        return fetchFromService("course-service", "/api/courses")
                .map(courses -> {
                    model.addAttribute("courses", courses);
                    model.addAttribute("activeTab", "enroll");
                    return "index";  // ← CHANGÉ
                });
    }

    @GetMapping("/ssr/student/{cnie}")
    public Mono<String> ssrStudentEnrollments(@PathVariable String cnie, Model model) {
        Mono<List> enrollmentsMono = fetchFromService("enrollment-service", "/api/enrollments/student/" + cnie);
        Mono<List> coursesMono     = fetchFromService("course-service", "/api/courses");

        return Mono.zip(enrollmentsMono, coursesMono)
                .map(tuple -> {
                    model.addAttribute("enrollments", tuple.getT1());
                    model.addAttribute("courses", tuple.getT2());
                    model.addAttribute("searchedCnie", cnie);
                    model.addAttribute("activeTab", "search");
                    return "index";  // ← CHANGÉ
                });
    }
}