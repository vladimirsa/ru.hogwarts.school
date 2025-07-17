package ru.hogwarts.school.controller;

import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.service.StudentService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/all")
    public List<StudentDTO> getAllStudents() {
        return studentService.getAllStudentDTOs();
    }

    @GetMapping("/{id}")
    public StudentDTO getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PostMapping
    public StudentDTO addStudent(@RequestBody StudentDTO studentDTO) {
        return studentService.addStudentDTO(studentDTO);
    }

    @PutMapping("/{id}")
    public StudentDTO updateStudent(@PathVariable Long id, @RequestBody StudentDTO studentDTO) {
        return studentService.updateStudentDTO(id, studentDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    @GetMapping("/filter-by-age")
    public List<StudentDTO> filterByAge(@RequestParam Integer age) {
        return studentService.filterByAge(age);
    }

    @GetMapping("/filter-by-age-range")
    public List<StudentDTO> filterByAgeRange(@RequestParam Integer min, @RequestParam Integer max) {
        return studentService.filterByAgeRange(min, max);
    }

    @GetMapping("/{id}/faculty")
    public FacultyDTO getFacultyByStudent(@PathVariable Long id) {
        return studentService.getFacultyByStudentId(id);
    }

    @GetMapping("/count")
    public long getStudentsCount() {
        return studentService.getStudentsCount();
    }

    @GetMapping("/average-age")
    public Double getAverageStudentAge() {
        return studentService.getAverageStudentAge();
    }

    @GetMapping("/last-five")
    public List<StudentDTO> getLastFiveStudents() {
        return studentService.getLastFiveStudents();
    }

}