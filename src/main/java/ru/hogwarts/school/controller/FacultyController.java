package ru.hogwarts.school.controller;

import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentShortDTO;
import ru.hogwarts.school.service.FacultyService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/all")
    public List<FacultyDTO> getAllFaculties() {
        return facultyService.getAllFacultyDTOs();
    }

    @GetMapping("/{id}")
    public FacultyDTO getFacultyById(@PathVariable Long id) {
        return facultyService.getFacultyById(id);
    }

    @PostMapping
    public FacultyDTO addFaculty(@RequestBody FacultyDTO facultyDTO) {
        return facultyService.addFacultyDTO(facultyDTO);
    }

    @PutMapping("/{id}")
    public FacultyDTO updateFaculty(@PathVariable Long id, @RequestBody FacultyDTO facultyDTO) {
        return facultyService.updateFacultyDTO(id, facultyDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
    }

    @GetMapping("/filter-by-name-or-color")
    public List<FacultyDTO> filterByNameOrColor(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color) {
        return facultyService.filterByNameOrColor(name, color);
    }

    @GetMapping("/{id}/students")
    public List<StudentShortDTO> getStudentsByFaculty(@PathVariable Long id) {
        return facultyService.getStudentsByFacultyId(id);
    }
}