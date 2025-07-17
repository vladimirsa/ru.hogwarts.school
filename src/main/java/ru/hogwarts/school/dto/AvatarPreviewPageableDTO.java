package ru.hogwarts.school.dto;

public class AvatarPreviewPageableDTO {
    private Long id;
    private String preview;
    private StudentShortDTO student;

    public AvatarPreviewPageableDTO(Long id, String preview, StudentShortDTO student) {
        this.id = id;
        this.preview = preview;
        this.student = student;
    }

    public Long getId() { return id; }
    public String getPreview() { return preview; }
    public StudentShortDTO getStudent() { return student; }
    public void setId(Long id) { this.id = id; }
    public void setPreview(String preview) { this.preview = preview; }
    public void setStudent(StudentShortDTO student) { this.student = student; }
}