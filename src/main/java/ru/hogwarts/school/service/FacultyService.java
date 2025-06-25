package ru.hogwarts.school.service;

import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentShortDTO;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    public FacultyService(FacultyRepository facultyRepository,
                          StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    private FacultyDTO toDTO(Faculty faculty) {
        List<StudentShortDTO> students = studentRepository.findAll().stream()
                .filter(s -> s.getFaculty() != null && s.getFaculty().getId().equals(faculty.getId()))
                .map(s -> new StudentShortDTO(s.getId(), s.getName(), s.getAge()))
                .collect(Collectors.toList());
        FacultyDTO dto = new FacultyDTO();
        dto.setId(faculty.getId());
        dto.setName(faculty.getName());
        dto.setColor(faculty.getColor());
        dto.setStudents(students);
        return dto;
    }

    public List<FacultyDTO> getAllFacultyDTOs() {
        return facultyRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public FacultyDTO getFacultyById(Long id) {
        return facultyRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public FacultyDTO addFacultyDTO(FacultyDTO dto) {
        Faculty f = new Faculty();
        f.setName(dto.getName());
        f.setColor(dto.getColor());
        Faculty saved = facultyRepository.save(f);
        return toDTO(saved);
    }

    public FacultyDTO updateFacultyDTO(Long id, FacultyDTO dto) {
        if (!facultyRepository.existsById(id)) return null;
        Faculty f = new Faculty();
        f.setId(id);
        f.setName(dto.getName());
        f.setColor(dto.getColor());
        Faculty updated = facultyRepository.save(f);
        return toDTO(updated);
    }

    public void deleteFaculty(Long id) {
        facultyRepository.deleteById(id);
    }

    public List<FacultyDTO> filterByNameOrColor(String name, String color) {
        return facultyRepository.findAll().stream()
                .filter(f -> (name != null && f.getName().equalsIgnoreCase(name))
                        || (color != null && f.getColor().equalsIgnoreCase(color)))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<StudentShortDTO> getStudentsByFacultyId(Long facultyId) {
        return studentRepository.findAll().stream()
                .filter(s -> s.getFaculty() != null && s.getFaculty().getId().equals(facultyId))
                .map(s -> new StudentShortDTO(s.getId(), s.getName(), s.getAge()))
                .collect(Collectors.toList());
    }
}