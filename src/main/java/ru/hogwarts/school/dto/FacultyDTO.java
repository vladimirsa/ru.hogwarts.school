package ru.hogwarts.school.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacultyDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    private String color;
    private List<StudentShortDTO> students;

    public FacultyDTO() {}

    public FacultyDTO(Long id, String name, String color, List<StudentShortDTO> students) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.students = students;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<StudentShortDTO> getStudents() {
        return students;
    }

    public void setStudents(List<StudentShortDTO> students) {
        this.students = students;
    }
}
