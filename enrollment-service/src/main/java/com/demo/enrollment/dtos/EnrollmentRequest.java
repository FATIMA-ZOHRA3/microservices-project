package com.demo.enrollment.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EnrollmentRequest {

    @NotBlank(message = "CNIE is required")
    private String cnie;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    public EnrollmentRequest() {}

    public String getCnie() { return cnie; }
    public void setCnie(String cnie) { this.cnie = cnie; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
}
