package kr.admin.serviceImpl;

import kr.admin.component.ImageModel;
import kr.admin.entity.ImageEntity;
import kr.admin.repository.ImageRepository;
import kr.admin.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository repository;

    @Override
    public ImageModel insertReceipt(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 없습니다.");
        }


        Path uploadPath = Paths.get("src/main/resources/static/image");
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String originalFileName = file.getOriginalFilename();
        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        }

        String storedFileName = System.currentTimeMillis() + "." + extension;

        Path filePath = uploadPath.resolve(storedFileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ImageEntity img = ImageEntity.builder()
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .extension(extension)
                .build();

        ImageEntity save = repository.save(img);

        ImageModel imageModel = ImageModel.builder()
                .originalFilename(save.getOriginalFileName())
                .storedFileName(save.getStoredFileName())
                .extension(save.getExtension())
                .build();

        return imageModel;
    }
}
