package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllStudents() throws Exception {
        Mockito.when(studentService.getAllStudentDTOs()).thenReturn(List.of(new StudentDTO()));
        mockMvc.perform(get("/student/all"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetStudentById() throws Exception {
        long studentId = 1L;
        String studentName = "Test";
        int studentAge = 20;
        StudentDTO dto = new StudentDTO(studentId, studentName, studentAge, null, null);
        Mockito.when(studentService.getStudentById(studentId)).thenReturn(dto);
        mockMvc.perform(get("/student/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId));
    }

    @Test
    void testAddStudent() throws Exception {
        String studentName = "Test";
        int studentAge = 20;
        Long savedId = 1L;

        StudentDTO dto = new StudentDTO(null, studentName, studentAge, null, null);
        StudentDTO saved = new StudentDTO(savedId, studentName, studentAge, null, null);
        Mockito.when(studentService.addStudentDTO(any())).thenReturn(saved);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedId));
    }

    @Test
    void testUpdateStudent() throws Exception {
        long studentId = 1L;
        String updatedName = "Updated";
        int updatedAge = 21;

        StudentDTO dto = new StudentDTO(null, updatedName, updatedAge, null, null);
        StudentDTO updated = new StudentDTO(studentId, updatedName, updatedAge, null, null);
        Mockito.when(studentService.updateStudentDTO(eq(studentId), any())).thenReturn(updated);

        mockMvc.perform(put("/student/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedName));
    }

    @Test
    void testDeleteStudent() throws Exception {
        long studentId = 1L;

        mockMvc.perform(delete("/student/{id}", studentId))
                .andExpect(status().isOk());
        Mockito.verify(studentService).deleteStudent(studentId);
    }

    @Test
    void testFilterByAge() throws Exception {
        int age = 20;
        long studentId = 1L;
        String studentName = "Test";
        StudentDTO student = new StudentDTO(studentId, studentName, age, null, null);

        Mockito.when(studentService.filterByAge(age)).thenReturn(List.of(student));
        mockMvc.perform(get("/student/filter-by-age").param("age", String.valueOf(age)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].age").value(age));
    }

    @Test
    void testFilterByAgeRange() throws Exception {
        int minAge = 18;
        int maxAge = 22;
        long studentId = 1L;
        String studentName = "Test";
        int studentAge = 20;
        StudentDTO student = new StudentDTO(studentId, studentName, studentAge, null, null);

        Mockito.when(studentService.filterByAgeRange(minAge, maxAge)).thenReturn(List.of(student));
        mockMvc.perform(get("/student/filter-by-age-range")
                        .param("min", String.valueOf(minAge))
                        .param("max", String.valueOf(maxAge)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].age").value(studentAge));
    }

    @Test
    void testGetFacultyByStudent() throws Exception {
        long facultyId = 1L;
        String facultyName = "Faculty";
        String facultyColor = "Color";
        Long studentId = 1L;

        FacultyDTO faculty = new FacultyDTO(facultyId, facultyName, facultyColor, null);
        Mockito.when(studentService.getFacultyByStudentId(studentId)).thenReturn(faculty);

        mockMvc.perform(get("/student/{id}/faculty", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(facultyId));
    }
}