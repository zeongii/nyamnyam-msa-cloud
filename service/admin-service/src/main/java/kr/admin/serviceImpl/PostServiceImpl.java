package kr.admin.serviceImpl;

import kr.admin.component.ImageModel;
import kr.admin.component.PostModel;
import kr.admin.entity.PostEntity;
import kr.admin.repository.PostTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl {
    private final PostTagRepository postTagRepository;


    public PostModel convertToModel(PostEntity entity) {
        return PostModel.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .taste(entity.getTaste())
                .clean(entity.getClean())
                .service(entity.getService())
                .entryDate(entity.getEntryDate())
                .modifyDate(entity.getModifyDate())
                .userId(entity.getUserId())
                .nickname(entity.getNickname()) // 닉네임 추가
                .restaurantId(entity.getRestaurant().getId()) // restaurantId 추가
                .averageRating((entity.getTaste() + entity.getClean() + entity.getService()) / 3.0)
                .tags(postTagRepository.findByPostId(entity.getId()).stream()
                        .map(postTagEntity -> postTagEntity.getTag().getName())
                        .collect(Collectors.toList()))
                .images(entity.getImages().stream()
                        .map(image -> ImageModel.builder()
                                .id(image.getId())
                                .originalFilename(image.getOriginalFileName())
                                .storedFileName(image.getStoredFileName())
                                .extension(image.getExtension())
                                .uploadURL(image.getUploadURL())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

}
