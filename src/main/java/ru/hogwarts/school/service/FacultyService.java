package ru.hogwarts.school.service;

import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentShortDTO;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FacultyService {

    private final Logger logger = LoggerFactory.getLogger(FacultyService.class);

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
        logger.info("Was invoked method for getAllFacultyDTOs");
        return facultyRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public FacultyDTO getFacultyById(Long id) {
        logger.info("Was invoked method for getFacultyById");
        return facultyRepository.findById(id)
                .map(this::toDTO)
                .orElseGet(() -> {
                    logger.warn("Faculty not found with id = {}", id);
                    return null;
                });
    }

    public FacultyDTO addFacultyDTO(FacultyDTO dto) {
        logger.info("Was invoked method for addFacultyDTO");
        Faculty f = new Faculty();
        f.setName(dto.getName());
        f.setColor(dto.getColor());
        Faculty saved = facultyRepository.save(f);
        logger.debug("Faculty saved with id = {}", saved.getId());
        return toDTO(saved);
    }

    public FacultyDTO updateFacultyDTO(Long id, FacultyDTO dto) {
        logger.info("Was invoked method for updateFacultyDTO");
        if (!facultyRepository.existsById(id)) {
            logger.error("Faculty not found for update with id = {}", id);
            return null;
        }
        Faculty f = new Faculty();
        f.setId(id);
        f.setName(dto.getName());
        f.setColor(dto.getColor());
        Faculty updated = facultyRepository.save(f);
        logger.debug("Faculty updated with id = {}", updated.getId());
        return toDTO(updated);
    }

    public void deleteFaculty(Long id) {
        logger.info("Was invoked method for deleteFaculty");
        if (!facultyRepository.existsById(id)) {
            logger.warn("Trying to delete non-existent faculty with id = {}", id);
        }
        facultyRepository.deleteById(id);
        logger.debug("Faculty deleted with id = {}", id);
    }

    public List<FacultyDTO> filterByNameOrColor(String name, String color) {
        logger.info("Was invoked method for filterByNameOrColor");
        List<FacultyDTO> result = facultyRepository.findAll().stream()
                .filter(f -> (name != null && f.getName().equalsIgnoreCase(name))
                        || (color != null && f.getColor().equalsIgnoreCase(color)))
                .map(this::toDTO)
                .collect(Collectors.toList());
        logger.debug("Filtered faculties count: {}", result.size());
        return result;
    }

    public List<StudentShortDTO> getStudentsByFacultyId(Long facultyId) {
        logger.info("Was invoked method for getStudentsByFacultyId");
        List<StudentShortDTO> students = studentRepository.findAll().stream()
                .filter(s -> s.getFaculty() != null && s.getFaculty().getId().equals(facultyId))
                .map(s -> new StudentShortDTO(s.getId(), s.getName(), s.getAge()))
                .collect(Collectors.toList());
        logger.debug("Found {} students for facultyId = {}", students.size(), facultyId);
        return students;
    }
}