package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.AvatarPreviewPageableDTO;
import ru.hogwarts.school.dto.StudentShortDTO;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Transactional
@Service
public class AvatarService {

    private final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Value("${path.to.avatars.folder}")
    private String avatarsDir;

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Was invoked method for uploadAvatar");
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.error("Student not found with id = " + studentId);
                    return new IllegalArgumentException("Student not found");
                });
        Path filePath = Path.of(avatarsDir, student + "." + getExtensions(file.getOriginalFilename()));
        logger.debug("Uploading avatar for studentId = {} to path {}", studentId, filePath);
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        try (
                InputStream is = file.getInputStream();
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        }
        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(new Avatar());
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(generateDataForDB(filePath));
        avatarRepository.save(avatar);
        logger.info("Avatar uploaded and saved for studentId = {}", studentId);
    }

    private byte[] generateDataForDB(Path filePath) throws IOException {
        logger.debug("Was invoked method for generateDataForDB");
        try (
            InputStream is = Files.newInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(is, 1024);
            ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                BufferedImage image = ImageIO.read(bis);
                if (image == null) {
                    logger.warn("ImageIO.read returned null for filePath: {}", filePath);
                }
                int height = image.getHeight() / (image.getWidth() / 100);
                BufferedImage preview = new BufferedImage(100, height, image.getType());
                Graphics2D graphics2D = preview.createGraphics();
                graphics2D.drawImage(image, 0, 0, 100, height, null);
                graphics2D.dispose();

                ImageIO.write(preview, getExtensions(filePath.getFileName().toString()), baos);
                return baos.toByteArray();
        }
    }

    private String getExtensions(String fileName) {
        logger.debug("Was invoked method for getExtensions");
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public Avatar getAvatarFromDb(Long studentId) {
        logger.info("Was invoked method for getAvatarFromDb");
        return avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> {
                    logger.error("Avatar not found for studentId = " + studentId);
                    return new IllegalArgumentException("Аватар не найден");
                });
    }

    public byte[] getAvatarFromFile(Long studentId) throws IOException {
        logger.info("Was invoked method for getAvatarFromFile");
        Avatar avatar = avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> {
                    logger.error("Avatar not found for studentId = " + studentId);
                    return new IllegalArgumentException("Аватар не найден");
                });
        Path path = Paths.get(avatar.getFilePath());
        logger.debug("Reading avatar file from path: {}", path);
        return Files.readAllBytes(path);
    }

    public String getAvatarMediaType(Long studentId) {
        logger.info("Was invoked method for getAvatarMediaType");
        return avatarRepository.findByStudentId(studentId)
                .map(Avatar::getMediaType)
                .orElse("application/octet-stream");
    }

    public Page<AvatarPreviewPageableDTO> getAvatarPreviews(int page, int size) {
        logger.info("Was invoked method for getAvatarPreviews");
        Page<Avatar> avatars = avatarRepository.findAll(PageRequest.of(page, size));
        List<AvatarPreviewPageableDTO> dtos = avatars.getContent().stream()
                .map(avatar -> new AvatarPreviewPageableDTO(
                        avatar.getId(),
                        Base64.getEncoder().encodeToString(avatar.getData()),
                        new StudentShortDTO(
                                avatar.getStudent().getId(),
                                avatar.getStudent().getName(),
                                avatar.getStudent().getAge()
                        )
                ))
                .collect(Collectors.toList());
        logger.debug("Returning {} avatar previews", dtos.size());
        return new PageImpl<>(dtos, avatars.getPageable(), avatars.getTotalElements());
    }


}