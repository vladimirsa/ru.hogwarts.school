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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class StudentService {

    private final Logger logger = LoggerFactory.getLogger(StudentService.class);

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
        logger.info("Was invoked method for getAllStudentDTOs");
        return studentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public StudentDTO getStudentById(Long id) {
        logger.info("Was invoked method for getStudentById");
        return studentRepository.findById(id)
                .map(this::toDTO)
                .orElseGet(() -> {
                    logger.warn("Student not found with id = {}", id);
                    return null;
                });
    }

    public StudentDTO addStudentDTO(StudentDTO dto) {
        logger.info("Was invoked method for addStudentDTO");
        Student saved = studentRepository.save(fromDTO(dto));
        logger.debug("Student saved with id = {}", saved.getId());
        return toDTO(saved);
    }

    public StudentDTO updateStudentDTO(Long id, StudentDTO dto) {
        logger.info("Was invoked method for updateStudentDTO");
        if (!studentRepository.existsById(id)) {
            logger.error("Student not found for update with id = {}", id);
            return null;
        }
        Student student = fromDTO(dto);
        student.setId(id);
        Student updated = studentRepository.save(student);
        logger.debug("Student updated with id = {}", updated.getId());
        return toDTO(updated);
    }

    public void deleteStudent(Long id) {
        logger.info("Was invoked method for deleteStudent");
        if (!studentRepository.existsById(id)) {
            logger.warn("Trying to delete non-existent student with id = {}", id);
        }
        studentRepository.deleteById(id);
        logger.debug("Student deleted with id = {}", id);
    }

    public List<StudentDTO> filterByAge(Integer age) {
        logger.info("Was invoked method for filterByAge");
        List<StudentDTO> result = getAllStudentDTOs().stream()
                .filter(s -> s.getAge() != null && s.getAge().equals(age))
                .collect(Collectors.toList());
        logger.debug("Filtered students by age count: {}", result.size());
        return result;
    }

    public List<StudentDTO> filterByAgeRange(Integer min, Integer max) {
        logger.info("Was invoked method for filterByAgeRange");
        List<StudentDTO> result = getAllStudentDTOs().stream()
                .filter(s -> s.getAge() != null && s.getAge() >= min && s.getAge() <= max)
                .collect(Collectors.toList());
        logger.debug("Filtered students by age range count: {}", result.size());
        return result;
    }

    public FacultyDTO getFacultyByStudentId(Long studentId) {
        logger.info("Was invoked method for getFacultyByStudentId");
        return studentRepository.findById(studentId)
                .map(Student::getFaculty)
                .map(faculty -> {
                    Faculty full = facultyRepository.findById(faculty.getId()).orElse(null);
                    if (full == null) {
                        logger.warn("Faculty not found for studentId = {}", studentId);
                        return null;
                    }
                    return new FacultyDTO(full.getId(), full.getName(), full.getColor(), null);
                })
                .orElseGet(() -> {
                    logger.warn("Student not found for faculty lookup with id = {}", studentId);
                    return null;
                });
    }

    public long getStudentsCount() {
        logger.info("Was invoked method for getStudentsCount");
        return studentRepository.countAllStudents();
    }

    public Double getAverageStudentAge() {
        logger.info("Was invoked method for getAverageStudentAge");
        return studentRepository.getAverageAge();
    }

    public List<StudentDTO> getLastFiveStudents() {
        logger.info("Was invoked method for getLastFiveStudents");
        List<StudentDTO> result = studentRepository.findLastFiveStudents().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        logger.debug("Last five students count: {}", result.size());
        return result;
    }
}