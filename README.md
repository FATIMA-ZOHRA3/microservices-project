**Course Enrollment Microservices**

A small course registration system built as a set of Spring Boot microservices. Students are registered, courses are managed, and students can enroll in a course under a few business rules. 

**Architecture**

There are five Spring Boot applications:

- eureka-server (port 8761): service discovery, so the other services find each other by name.
- student-service (port 8081): creates and manages students, identified by their CNIE. Uses its own MySQL database, student_db.
- course-service (port 8082): CRUD on courses (title, description, credits). Database: course_db.
- enrollment-service (port 8083): handles enrollments. It calls the student and course services through the discovery server to check that the student and the course exist before saving anything. Database: enrollment_db.
- api-gateway (port 8080): single entry point, built with Spring Cloud Gateway. It routes /api/students, /api/courses and /api/enrollments to the right service, and also serves a small web interface rendered with Thymeleaf.

Each service owns its data and only talks to the others over HTTP, so an enrollment only stores a student CNIE and a course id, not a copy of their data.

**Business rules**

A course accepts at most 3 students. A student cannot enroll twice in the same course. An enrollment can only be cancelled within 24 hours, and the API tells the client whether an enrollment is still cancellable.

**API**

Everything goes through the gateway on port 8080.

- GET, POST /api/students, and GET, PUT, DELETE /api/students/{id}
- GET /api/students/cnie/{cnie}
- GET, POST /api/courses, and GET, PUT, DELETE /api/courses/{id}
- POST /api/enrollments with a body like {"cnie": "CD2387", "courseId": 1}
- GET /api/enrollments/student/{cnie} to list one student's enrollments
- DELETE /api/enrollments/{id}?cnie=CD2387 to cancel

The web interface is available at http://localhost:8080 and shows the students, the courses, an enrollment form and each student's enrollments.

**Built with**

Java 17, Spring Boot 3.2, Spring Cloud (Gateway, Netflix Eureka, LoadBalancer), Spring Data JPA, WebClient, Thymeleaf, MySQL 8, Maven.

**Running it**

You need Java 17, Maven and a local MySQL server. The databases are created automatically on first start.

The services read their MySQL credentials from their application.yml. student-service expects two environment variables, DB_USERNAME and DB_PASSWORD, so set them first, for example:

```bash
export DB_USERNAME=root
export DB_PASSWORD=root
```

Then start the services in this order, each in its own terminal:

```bash
cd eureka-server && mvn spring-boot:run
cd student-service && mvn spring-boot:run
cd course-service && mvn spring-boot:run
cd enrollment-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
```

Check that all four services appear on the Eureka dashboard at http://localhost:8761, then open http://localhost:8080.


**Author**

Fatima Zohra Bentouhami, [LinkedIn](https://www.linkedin.com/in/fatima-zohra-bentouhami-48b4a3309)
