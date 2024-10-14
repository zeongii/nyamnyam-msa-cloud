package kr.user.serviceImpl;

import kr.user.document.User;
import kr.user.document.UsersThumbnail;
import kr.user.repository.UserThumbnailRepository;
import kr.user.service.UserThumbnailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Service
@RequiredArgsConstructor
public class UserThumbnailServiceImpl implements UserThumbnailService {

    private final UserThumbnailRepository userThumbnailRepository;

    @Override
    public Mono<List<String>> uploadThumbnail(User user, List<MultipartFile> images) {
        List<String> thumbnailIds = new ArrayList<>();

        return Flux.fromIterable(images)
                .flatMap(image -> {
                    return Mono.fromCallable(() -> {
                                String uploadsDir = "src/main/resources/static/uploads/thumbnails/";
                                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                                String filePath = uploadsDir + fileName;

                                File dest = new File(filePath);
                                image.transferTo(dest);

                                UsersThumbnail thumbnail = UsersThumbnail.builder()
                                        .userId(user.getId())
                                        .thumbnailUrl(filePath)
                                        .createdAt(LocalDateTime.now().toEpochSecond(null))
                                        .build();

                                return thumbnail;
                            })
                            .flatMap(thumbnail ->
                                    userThumbnailRepository.save(thumbnail)
                                            .map(savedThumbnail -> {
                                                thumbnailIds.add(savedThumbnail.getId());
                                                return savedThumbnail.getId();
                                            })
                            )
                            .onErrorResume(e -> Mono.error(new RuntimeException("Failed to save thumbnail: " + e.getMessage())));
                })
                .then(Mono.just(thumbnailIds))
                .onErrorResume(e -> Mono.just(Collections.emptyList()));
    }

}


