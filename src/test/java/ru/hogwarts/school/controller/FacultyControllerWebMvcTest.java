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
import ru.hogwarts.school.dto.StudentShortDTO;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
public class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllFaculties() throws Exception {
        Mockito.when(facultyService.getAllFacultyDTOs()).thenReturn(List.of(new FacultyDTO()));
        mockMvc.perform(get("/faculty/all"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetFacultyById() throws Exception {
        long facultyId = 1L;
        String facultyName = "Test Faculty";
        String facultyColor = "Red";
        FacultyDTO dto = new FacultyDTO(facultyId, facultyName, facultyColor, null);
        Mockito.when(facultyService.getFacultyById(facultyId)).thenReturn(dto);
        mockMvc.perform(get("/faculty/{id}", facultyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(facultyId));
    }

    @Test
    void testAddFaculty() throws Exception {
        String facultyName = "Add Faculty";
        String facultyColor = "Green";
        Long savedId = 1L;

        FacultyDTO dto = new FacultyDTO(null, facultyName, facultyColor, null);
        FacultyDTO saved = new FacultyDTO(savedId, facultyName, facultyColor, null);
        Mockito.when(facultyService.addFacultyDTO(any())).thenReturn(saved);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedId));
    }

    @Test
    void testUpdateFaculty() throws Exception {
        long facultyId = 1L;
        String updatedName = "Updated Faculty";
        String updatedColor = "Blue";

        FacultyDTO dto = new FacultyDTO(null, updatedName, updatedColor, null);
        FacultyDTO updated = new FacultyDTO(facultyId, updatedName, updatedColor, null);
        Mockito.when(facultyService.updateFacultyDTO(eq(facultyId), any())).thenReturn(updated);

        mockMvc.perform(put("/faculty/{id}", facultyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedName));
    }

    @Test
    void testDeleteFaculty() throws Exception {
        long facultyId = 1L;

        mockMvc.perform(delete("/faculty/{id}", facultyId))
                .andExpect(status().isOk());
        Mockito.verify(facultyService).deleteFaculty(facultyId);
    }

    @Test
    void testFilterByNameOrColor() throws Exception {
        String facultyName = "Color Filter";
        String facultyColor = "Purple";
        long facultyId = 1L;

        FacultyDTO faculty = new FacultyDTO(facultyId, facultyName, facultyColor, null);
        Mockito.when(facultyService.filterByNameOrColor(eq(null), eq(facultyColor))).thenReturn(List.of(faculty));

        mockMvc.perform(get("/faculty/filter-by-name-or-color")
                        .param("color", facultyColor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].color").value(facultyColor));
    }

    @Test
    void testGetStudentsByFaculty() throws Exception {
        long facultyId = 1L;
        long studentId = 2L;
        String studentName = "Student In Faculty";
        int studentAge = 19;

        StudentShortDTO student = new StudentShortDTO(studentId, studentName, studentAge);
        Mockito.when(facultyService.getStudentsByFacultyId(facultyId)).thenReturn(List.of(student));

        mockMvc.perform(get("/faculty/{id}/students", facultyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(studentName));
    }
}