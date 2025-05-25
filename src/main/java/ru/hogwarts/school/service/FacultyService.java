package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FacultyService {
    private final Map<Long, Faculty> faculties = new HashMap<>();
    private long idCounter = 1;

    public Faculty addFaculty(Faculty faculty) {
        faculty.setId(idCounter++);
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty getFaculty(Long id) {
        return faculties.get(id);
    }

    public List<Faculty> getAllFaculties() {
        return new ArrayList<>(faculties.values());
    }

    public Faculty updateFaculty(Long id, Faculty updatedFaculty) {
        if (faculties.containsKey(id)) {
            updatedFaculty.setId(id);
            faculties.put(id, updatedFaculty);
            return updatedFaculty;
        }
        return null;
    }

    public List<Faculty> getFacultiesByColor(String color) {
        List<Faculty> result = new ArrayList<>();
        for (Faculty faculty : faculties.values()) {
            if (faculty.getColor().equalsIgnoreCase(color)) {
                result.add(faculty);
            }
        }
        return result;
    }

    public boolean deleteFaculty(Long id) {
        return faculties.remove(id) != null;
    }
}