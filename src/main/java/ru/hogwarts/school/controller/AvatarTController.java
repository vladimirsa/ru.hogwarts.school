package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.AvatarPreviewPageableDTO;
import ru.hogwarts.school.dto.StudentShortDTO;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.repository.AvatarRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Base64;

@RestController
@RequestMapping("/avatar-t")
public class AvatarTController {

    private final AvatarRepository avatarRepository;

    @Autowired
    public AvatarTController(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    @GetMapping("/page")
    public Page<AvatarPreviewPageableDTO> getAvatarsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Avatar> avatars = avatarRepository.findAll(PageRequest.of(page, size));
        List<AvatarPreviewPageableDTO> dtos = avatars.stream()
                .map(avatar -> new AvatarPreviewPageableDTO(
                        avatar.getId(),
                        avatar.getData() != null ? Base64.getEncoder().encodeToString(avatar.getData()) : null,
                        avatar.getStudent() != null
                                ? new StudentShortDTO(
                                avatar.getStudent().getId(),
                                avatar.getStudent().getName(),
                                avatar.getStudent().getAge())
                                : null
                ))
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, avatars.getPageable(), avatars.getTotalElements());
    }
}