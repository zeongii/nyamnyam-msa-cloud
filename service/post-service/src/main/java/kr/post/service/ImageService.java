package kr.post.service;

import kr.post.component.ImageModel;
import kr.post.entity.ImageEntity;
import kr.post.entity.PostEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ImageService {

    List<ImageEntity> findAll();

    Optional<ImageEntity> findById(Long id);

    Boolean existsById(Long id);

    Boolean deleteById(Long id);

    Boolean save(ImageEntity entity);

    String getFileName(String fileName);

    List<ImageModel> uploadFiles(List<MultipartFile> multipartFiles, PostEntity postEntity);

    ImageModel insertReceipt(MultipartFile file);

    List<Long> findImageIdsByPostId(Long postId);

    List<ImageEntity> findByPostId(Long postId);

    Boolean updateImages(Long postId, List<MultipartFile> multipartFiles, List<Long> imagesToDelete);

    List<String> findImagesByRestaurantId(Long restaurantId);
}