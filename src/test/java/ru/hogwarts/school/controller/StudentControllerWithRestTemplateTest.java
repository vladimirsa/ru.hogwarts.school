package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StudentControllerWithRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() throws Exception {
        Assertions.assertThat(studentController).isNotNull();
    }

    @Test
    public void testGetAllStudents() {
        String url = "http://localhost:" + port + "/student/all";
        var response = restTemplate.getForEntity(url, String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void testGetStudentById() {
        StudentDTO student = new StudentDTO();
        student.setName("Get StudentById");
        student.setAge(20);
        var created = restTemplate.postForEntity("http://localhost:" + port + "/student", student, StudentDTO.class);
        Assertions.assertThat(created.getStatusCode()).isEqualTo(HttpStatus.OK);
        Long id = created.getBody().getId();
        String url = "http://localhost:" + port + "/student/" + id;
        var response = restTemplate.getForEntity(url, StudentDTO.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isEqualTo(id);

        restTemplate.delete("http://localhost:" + port + "/student/" + id);
    }

    @Test
    public void testAddStudent() {
        StudentDTO student = new StudentDTO();
        student.setName("Add Student");
        student.setAge(21);
        var created = restTemplate.postForEntity("http://localhost:" + port + "/student", student, StudentDTO.class);
        Assertions.assertThat(created.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(created.getBody()).isNotNull();
        Assertions.assertThat(created.getBody().getId()).isNotNull();
        Assertions.assertThat(created.getBody().getName()).isEqualTo("Add Student");

        Long id = created.getBody().getId();
        restTemplate.delete("http://localhost:" + port + "/student/" + id);
    }

    @Test
    public void testUpdateStudent() {
        StudentDTO student = new StudentDTO();
        student.setName("Update Student");
        student.setAge(22);
        var created = restTemplate.postForEntity("http://localhost:" + port + "/student", student, StudentDTO.class);
        Long id = created.getBody().getId();

        StudentDTO updated = new StudentDTO();
        updated.setName("Updated Name");
        updated.setAge(23);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<StudentDTO> entity = new HttpEntity<>(updated, headers);

        var response = restTemplate.exchange(
                "http://localhost:" + port + "/student/" + id,
                HttpMethod.PUT,
                entity,
                StudentDTO.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getName()).isEqualTo("Updated Name");
        Assertions.assertThat(response.getBody().getAge()).isEqualTo(23);

        restTemplate.delete("http://localhost:" + port + "/student/" + id);
    }

    @Test
    public void testDeleteStudent() {
        StudentDTO student = new StudentDTO();
        student.setName("Delete Student");
        student.setAge(24);
        var created = restTemplate.postForEntity("http://localhost:" + port + "/student", student, StudentDTO.class);
        Long id = created.getBody().getId();
        System.out.println("Created Student ID: " + id);
        restTemplate.delete("http://localhost:" + port + "/student/" + id);

        var response = restTemplate.getForEntity("http://localhost:" + port + "/student/" + id, StudentDTO.class);
        System.out.println("Response after deletion: " + response);
        Assertions.assertThat(response.getBody()).isNull();
    }

    @Test
    public void testFilterByAge() {
        StudentDTO student = new StudentDTO();
        student.setName("Age Filter");
        student.setAge(25);
        var created = restTemplate.postForEntity("http://localhost:" + port + "/student", student, StudentDTO.class);
        Long id = created.getBody().getId();
        String url = "http://localhost:" + port + "/student/filter-by-age?age=25";
        var response = restTemplate.getForEntity(url, StudentDTO[].class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotEmpty();
        Assertions.assertThat(response.getBody()[0].getAge()).isEqualTo(25);

        restTemplate.delete("http://localhost:" + port + "/student/" + id);
    }

    @Test
    public void testFilterByAgeRange() {
        StudentDTO student = new StudentDTO();
        student.setName("Age Range Filter");
        student.setAge(30);
        var created = restTemplate.postForEntity("http://localhost:" + port + "/student", student, StudentDTO.class);
        Long id = created.getBody().getId();

        String url = "http://localhost:" + port + "/student/filter-by-age-range?min=29&max=31";
        var response = restTemplate.getForEntity(url, StudentDTO[].class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotEmpty();
        Assertions.assertThat(response.getBody()[0].getAge()).isBetween(29, 31);

        restTemplate.delete("http://localhost:" + port + "/student/" + id);
    }

    @Test
    public void testGetFacultyByStudent() {

        FacultyDTO faculty = new FacultyDTO();
        faculty.setName("Test Faculty");
        faculty.setColor("Blue");
        var facultyCreated = restTemplate.postForEntity("http://localhost:" + port + "/faculty", faculty, FacultyDTO.class);
        Assertions.assertThat(facultyCreated.getStatusCode()).isEqualTo(HttpStatus.OK);
        Long facultyId = facultyCreated.getBody().getId();
        Assertions.assertThat(facultyId).isNotNull();
        Assertions.assertThat(facultyCreated.getBody().getName()).isEqualTo("Test Faculty");
        Assertions.assertThat(facultyCreated.getBody().getColor()).isEqualTo("Blue");

        StudentDTO student = new StudentDTO();
        student.setName("Faculty Student");
        student.setAge(26);
        student.setFacultyId(facultyId);
        var created = restTemplate.postForEntity("http://localhost:" + port + "/student", student, StudentDTO.class);
        Long id = created.getBody().getId();
        System.out.println("Created Student ID: " + id);

        String url = "http://localhost:" + port + "/student/" + id + "/faculty";
        var response = restTemplate.getForEntity(url, FacultyDTO.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isEqualTo(facultyId);

        restTemplate.delete("http://localhost:" + port + "/student/" + id);
        restTemplate.delete("http://localhost:" + port + "/faculty/" + facultyId);
    }
}