package com.demo.student.dtos;

/**
 * DTO exposed to other services and the frontend.
 * Never exposes the internal DB id directly to clients.
 */
public class StudentDTO {

    private Long id;
    private String cnie;
    private String firstName;
    private String lastName;
    private String email;

    public StudentDTO() {}

    public StudentDTO(Long id, String cnie, String firstName, String lastName, String email) {
        this.id = id;
        this.cnie = cnie;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getCnie() { return cnie; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }

    public void setId(Long id) { this.id = id; }
    public void setCnie(String cnie) { this.cnie = cnie; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
}
