package ru.hogwarts.school.service;

import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public StudentService(StudentRepository studentRepository,
                          FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    private StudentDTO toDTO(Student student) {
        Long facultyId = null;
        String facultyName = null;
        String facultyColor = null;
        if (student.getFaculty() != null) {
            facultyId = student.getFaculty().getId();
            Faculty faculty = facultyRepository.findById(facultyId).orElse(null);
            if (faculty != null) {
                facultyName = faculty.getName();
                facultyColor = faculty.getColor();
            }
        }
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setAge(student.getAge());
        dto.setFacultyId(facultyId);
        dto.setFacultyName(facultyName);
        dto.setFacultyColor(facultyColor);
        return dto;
    }

    private Student fromDTO(StudentDTO dto) {
        Student student = new Student();
        student.setName(dto.getName());
        student.setAge(dto.getAge());
        if (dto.getFacultyId() != null) {
            Faculty f = new Faculty();
            f.setId(dto.getFacultyId());
            student.setFaculty(f);
        }
        return student;
    }

    public List<StudentDTO> getAllStudentDTOs() {
        return studentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public StudentDTO getStudentById(Long id) {
        return studentRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public StudentDTO addStudentDTO(StudentDTO dto) {
        Student saved = studentRepository.save(fromDTO(dto));
        return toDTO(saved);
    }

    public StudentDTO updateStudentDTO(Long id, StudentDTO dto) {
        if (!studentRepository.existsById(id)) {
            return null;
        }
        Student student = fromDTO(dto);
        student.setId(id);
        Student updated = studentRepository.save(student);
        return toDTO(updated);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public List<StudentDTO> filterByAge(Integer age) {
        return getAllStudentDTOs().stream()
                .filter(s -> s.getAge() != null && s.getAge().equals(age))
                .collect(Collectors.toList());
    }

    public List<StudentDTO> filterByAgeRange(Integer min, Integer max) {
        return getAllStudentDTOs().stream()
                .filter(s -> s.getAge() != null && s.getAge() >= min && s.getAge() <= max)
                .collect(Collectors.toList());
    }

    public FacultyDTO getFacultyByStudentId(Long studentId) {
        return studentRepository.findById(studentId)
                .map(Student::getFaculty)
                .map(faculty -> {
                    Faculty full = facultyRepository.findById(faculty.getId()).orElse(null);
                    if (full == null) return null;
                    return new FacultyDTO(full.getId(), full.getName(), full.getColor(), null);
                })
                .orElse(null);
    }
}