package ru.hogwarts.school.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;
import java.io.IOException;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/{studentId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadAvatar(@PathVariable Long studentId, @RequestParam MultipartFile avatar) throws IOException {
        avatarService.uploadAvatar(studentId, avatar);
        return "Avatar загружен успешно";
    }

    @GetMapping("/{studentId}/preview")
    public ResponseEntity<byte[]> getAvatarPreview(@PathVariable Long studentId) {
        Avatar avatar = avatarService.getAvatarFromDb(studentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(avatar.getData());
    }

    @GetMapping("/{studentId}/file")
    public void downloadAvatarFile(@PathVariable Long studentId, HttpServletResponse response) throws IOException {
        byte[] data = avatarService.getAvatarFromFile(studentId);
        String mediaType = avatarService.getAvatarMediaType(studentId);
        response.setContentType(mediaType);
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
    }
}
