package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.model.Faculty;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudent(Long id) {
        return studentRepository.findById(id);
    }

    public List<Student> getStudentsByAge(int age) {
        return studentRepository.findByAge(age);
    }

    public List<Student> getStudentsByAgeRange(int min, int max) {
        return studentRepository.findByAgeBetween(min, max);
    }

    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, Student student) {
        if (studentRepository.existsById(id)) {
            student.setId(id);
            return studentRepository.save(student);
        }
        return null;
    }

    public boolean deleteStudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public Optional<Faculty> getFacultyByStudentId(Long studentId) {
        return getStudent(studentId)
                .map(student -> {
                    Faculty faculty = student.getFaculty();
                    if (faculty != null) {
                        Hibernate.initialize(faculty);
                    }
                    return faculty;
                });
    }

    public List<Student> getStudentsByFacultyId(Long facultyId) {
        return studentRepository.findAll().stream()
                .filter(student -> student.getFaculty() != null && facultyId.equals(student.getFaculty().getId()))
                .collect(Collectors.toList());
    }

}