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
import ru.hogwarts.school.dto.StudentShortDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FacultyControllerWithRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetAllFaculties() {
        String url = "http://localhost:" + port + "/faculty/all";
        var response = restTemplate.getForEntity(url, FacultyDTO[].class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testGetFacultyById() {
        FacultyDTO faculty = new FacultyDTO();
        faculty.setName("Test Faculty");
        faculty.setColor("Red");
        var created = restTemplate.postForEntity("http://localhost:" + port + "/faculty", faculty, FacultyDTO.class);
        Long id = created.getBody().getId();

        String url = "http://localhost:" + port + "/faculty/" + id;
        var response = restTemplate.getForEntity(url, FacultyDTO.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isEqualTo(id);

        restTemplate.delete("http://localhost:" + port + "/faculty/" + id);
    }

    @Test
    public void testAddFaculty() {
        FacultyDTO faculty = new FacultyDTO();
        faculty.setName("Add Faculty");
        faculty.setColor("Green");
        var response = restTemplate.postForEntity("http://localhost:" + port + "/faculty", faculty, FacultyDTO.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getName()).isEqualTo("Add Faculty");

        Long id = response.getBody().getId();
        restTemplate.delete("http://localhost:" + port + "/faculty/" + id);
    }

    @Test
    public void testUpdateFaculty() {
        FacultyDTO faculty = new FacultyDTO();
        faculty.setName("To Update");
        faculty.setColor("Yellow");
        var created = restTemplate.postForEntity("http://localhost:" + port + "/faculty", faculty, FacultyDTO.class);
        Long id = created.getBody().getId();

        FacultyDTO updated = new FacultyDTO();
        updated.setName("Updated Faculty");
        updated.setColor("Blue");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FacultyDTO> entity = new HttpEntity<>(updated, headers);

        var response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/" + id,
                HttpMethod.PUT,
                entity,
                FacultyDTO.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getName()).isEqualTo("Updated Faculty");

        restTemplate.delete("http://localhost:" + port + "/faculty/" + id);
    }

    @Test
    public void testDeleteFaculty() {
        FacultyDTO faculty = new FacultyDTO();
        faculty.setName("To Delete");
        faculty.setColor("Black");
        var created = restTemplate.postForEntity("http://localhost:" + port + "/faculty", faculty, FacultyDTO.class);
        Long id = created.getBody().getId();

        restTemplate.delete("http://localhost:" + port + "/faculty/" + id);

        var response = restTemplate.getForEntity("http://localhost:" + port + "/faculty/" + id, FacultyDTO.class);
        Assertions.assertThat(response.getBody()).isNull();
    }

    @Test
    public void testFilterByNameOrColor() {
        FacultyDTO faculty = new FacultyDTO();
        faculty.setName("Color Filter");
        faculty.setColor("purple");
        var created = restTemplate.postForEntity("http://localhost:" + port + "/faculty", faculty, FacultyDTO.class);
        Long id = created.getBody().getId();

        String url = "http://localhost:" + port + "/faculty/filter-by-name-or-color?color=Purple";
        var response = restTemplate.getForEntity(url, FacultyDTO[].class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody()).isNotEmpty();
        Assertions.assertThat(response.getBody()[0].getColor()).isEqualTo("purple");

        restTemplate.delete("http://localhost:" + port + "/faculty/" + id);
    }

    @Test
    public void testGetStudentsByFaculty() {

        FacultyDTO faculty = new FacultyDTO();
        faculty.setName("Faculty With Students");
        faculty.setColor("Orange");
        var facultyCreated = restTemplate.postForEntity("http://localhost:" + port + "/faculty", faculty, FacultyDTO.class);
        Long facultyId = facultyCreated.getBody().getId();

        var student = new StudentDTO();
        student.setName("Student In Faculty");
        student.setAge(19);
        student.setFacultyId(facultyId);
        var studentCreated = restTemplate.postForEntity("http://localhost:" + port + "/student", student, ru.hogwarts.school.dto.StudentDTO.class);
        Long studentId = studentCreated.getBody().getId();

        String url = "http://localhost:" + port + "/faculty/" + facultyId + "/students";
        var response = restTemplate.getForEntity(url, StudentShortDTO[].class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody()).isNotEmpty();
        Assertions.assertThat(response.getBody()[0].getName()).isEqualTo("Student In Faculty");

        restTemplate.delete("http://localhost:" + port + "/student/" + studentId);
        restTemplate.delete("http://localhost:" + port + "/faculty/" + facultyId);
    }
}
