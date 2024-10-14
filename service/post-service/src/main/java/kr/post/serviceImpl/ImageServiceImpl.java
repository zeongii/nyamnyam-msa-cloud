package kr.post.serviceImpl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import kr.post.component.ImageModel;
import kr.post.entity.ImageEntity;
import kr.post.entity.PostEntity;
import kr.post.repository.ImageRepository;
import kr.post.repository.PostRepository;
import kr.post.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository repository;
    private final PostServiceImpl postService;
    private final AmazonS3 amazonS3;
    private final PostRepository postRepository;

    @Value("${naver.storage.bucket}")
    private String bucketName;

    @Value("${naver.storage.upload.path}")
    private String uploadPath;

    @Override
    public String getFileName(String fileName) {
        String ext = fileName.substring(fileName.indexOf("."));
        return System.currentTimeMillis() + ext;
    }

    @Override
    public List<ImageModel> uploadFiles(List<MultipartFile> multipartFiles, PostEntity postEntity) {
        List<ImageModel> s3files = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String originalFilename = multipartFile.getOriginalFilename();
            String storedFileName = getFileName(originalFilename);
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            }
            String uploadURL = "";

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            objectMetadata.setContentType(multipartFile.getContentType());

            try (InputStream inputStream = multipartFile.getInputStream()) {
                String keyName = uploadPath + "/" + storedFileName;

                amazonS3.putObject(
                        new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata)
                                .withCannedAcl(CannedAccessControlList.PublicRead));

                uploadURL = "https://kr.object.ncloudstorage.com/" + bucketName + "/" + keyName;
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
            }
            ImageModel imageModel = ImageModel.builder()
                    .originalFilename(originalFilename)
                    .storedFileName(storedFileName)
                    .extension(extension)
                    .uploadPath(uploadPath)
                    .uploadURL(uploadURL)
                    .postId(postEntity.getId())
                    .build();

            ImageEntity imageEntity = convertToEntity(imageModel);
            imageEntity.setPost(postEntity);

            ImageEntity savedEntity = repository.save(imageEntity);

            imageModel.setId(savedEntity.getId());
            s3files.add(imageModel);
        }
        return s3files;
    }

    @Override
    public List<String> findImagesByRestaurantId(Long restaurantId) {
        List<PostEntity> postEntity = postRepository.findByRestaurantId(restaurantId);
        List<Long> postIds = postEntity.stream()
                .map(PostEntity::getId)
                .collect(Collectors.toList());

        return repository.findByPostIdIn(postIds).stream()
                .map(ImageEntity::getUploadURL)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> findImageIdsByPostId(Long postId) {
        return repository.findImageIdsByPostId(postId);
    }

    @Override
    public List<ImageEntity> findByPostId(Long postId) {
        return repository.findByPostId(postId);
    }

    @Override
    public Boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Boolean updateImages(Long postId, List<MultipartFile> multipartFiles, List<Long> imagesToDelete) {
        if(imagesToDelete != null && !imagesToDelete.isEmpty()) {
            List<ImageEntity> existImages = repository.findByPostId(postId);

            for (ImageEntity image : existImages) {
                if(imagesToDelete.contains(image.getId())) {
                    try {
                        String keyName = image.getUploadURL().replace("https://kr.object.ncloudstorage.com/" + bucketName + "/", "");
                        amazonS3.deleteObject(bucketName, keyName);
                        repository.delete(image);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to delete image: " + image.getStoredFileName());
                    }
                }
            }
        }
        if(multipartFiles != null && !multipartFiles.isEmpty()) {
            PostEntity postEntity = postService.findEntityById(postId);
            for (MultipartFile multipartFile : multipartFiles) {
                String originalFilename = multipartFile.getOriginalFilename();
                String storedFileName = getFileName(originalFilename);
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                }
                String uploadURL = "";

                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(multipartFile.getSize());
                objectMetadata.setContentType(multipartFile.getContentType());

                try (InputStream inputStream = multipartFile.getInputStream()) {
                    String keyName = uploadPath + storedFileName;

                    amazonS3.putObject(
                            new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata)
                                    .withCannedAcl(CannedAccessControlList.PublicRead)
                    );
                    uploadURL = "https://kr.object.ncloudstorage.com/" + bucketName + "/" + keyName;
                } catch (IOException e) {
                    throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
                }
                ImageEntity imageEntity = ImageEntity.builder()
                        .originalFileName(originalFilename)
                        .storedFileName(storedFileName)
                        .extension(extension)
                        .uploadPath(uploadPath)
                        .uploadURL(uploadURL)
                        .post(postEntity)
                        .build();

                repository.save(imageEntity);
            }
        }
        return true;
    }

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

    @Override
    public List<ImageEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<ImageEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Boolean deleteById(Long imgId) {
        if (repository.existsById(imgId)) {
            repository.deleteById(imgId);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean save(ImageEntity entity) {
        repository.save(entity);
        return true;
    }

    private ImageEntity convertToEntity(ImageModel model) {
        PostEntity postEntity = postService.findEntityById(model.getPostId());

        return ImageEntity.builder()
                .originalFileName(model.getOriginalFilename())
                .storedFileName(model.getStoredFileName())
                .extension(model.getExtension())
                .uploadPath(model.getUploadPath())
                .uploadURL(model.getUploadURL())
                .build();
    }
}
