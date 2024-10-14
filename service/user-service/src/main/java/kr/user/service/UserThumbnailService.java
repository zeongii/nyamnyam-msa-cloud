package kr.user.service;

import kr.user.document.User;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserThumbnailService {
    Mono<List<String>> uploadThumbnail(User user, List<MultipartFile> images);
}

