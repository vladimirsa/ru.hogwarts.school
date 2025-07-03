package ru.hogwarts.school.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StudentDTO {
    private Long id;
    private String name;
    private Integer age;
    private Long facultyId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String facultyName;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String facultyColor;

    public StudentDTO() {}

    public StudentDTO(Long id, String name, Integer age, Long facultyId, String facultyName) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.facultyId = facultyId;
        this.facultyName = facultyName;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Long facultyId) {
        this.facultyId = facultyId;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getFacultyColor() {
        return facultyColor;
    }

    public void setFacultyColor(String facultyColor) {
        this.facultyColor = facultyColor;
    }

    @Override
    public String toString() {
        return "StudentDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", facultyId=" + facultyId +
                ", facultyName='" + facultyName + '\'' +
                ", facultyColor='" + facultyColor + '\'' +
                '}';
    }
}
