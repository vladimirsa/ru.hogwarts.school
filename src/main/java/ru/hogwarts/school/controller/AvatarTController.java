package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.AvatarPreviewPageableDTO;
import ru.hogwarts.school.service.AvatarService;

@RestController
@RequestMapping("/avatar-t")
public class AvatarTController {

    private final AvatarService avatarService;

    @Autowired
    public AvatarTController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping("/previews")
    public Page<AvatarPreviewPageableDTO> getAvatarPreviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return avatarService.getAvatarPreviews(page, size);
    }
}