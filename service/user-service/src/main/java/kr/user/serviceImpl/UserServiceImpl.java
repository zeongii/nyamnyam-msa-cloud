package kr.user.serviceImpl;

import kr.user.document.User;
import kr.user.repository.UserRepository;
import kr.user.service.TokenService;
import kr.user.service.UserService;
import kr.user.service.UserThumbnailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final UserThumbnailService userThumbnailService;

    @Override
    public Mono<Boolean> existsById(String id) {
        return userRepository.existsById(id);
    }

    @Override
    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Mono<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Mono<Long> count() {
        return userRepository.count();
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return userRepository.deleteById(id);
    }

    @Override
    public Mono<User> update(User user, List<MultipartFile> thumbnails) {
        return userRepository.findById(user.getId())
                .flatMap(existingUser -> {
                    existingUser.setUsername(user.getUsername() != null ? user.getUsername() : existingUser.getUsername());
                    existingUser.setPassword(user.getPassword() != null ? user.getPassword() : existingUser.getPassword());
                    existingUser.setNickname(user.getNickname() != null ? user.getNickname() : existingUser.getNickname());
                    existingUser.setName(user.getName() != null ? user.getName() : existingUser.getName());
                    existingUser.setAge(user.getAge() != null ? user.getAge() : existingUser.getAge());
                    existingUser.setRole(user.getRole() != null ? user.getRole() : existingUser.getRole());
                    existingUser.setTel(user.getTel() != null ? user.getTel() : existingUser.getTel());
                    existingUser.setGender(user.getGender() != null ? user.getGender() : existingUser.getGender());
                    existingUser.setEnabled(user.getEnabled() != null ? user.getEnabled() : existingUser.getEnabled());

                    // 썸네일 업데이트 처리
                    return userThumbnailService.uploadThumbnail(existingUser, thumbnails)
                            .then(userRepository.save(existingUser));
                })
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")));
    }

    @Override
    public Mono<User> save(User user, List<MultipartFile> thumbnails) {
        return userRepository.findByUsername(user.getUsername())
                .flatMap(existingUser -> Mono.<User>error(new RuntimeException("Username is already taken.")))
                .switchIfEmpty(
                        Mono.defer(() -> {
                            String encodedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
                            User newUser = User.builder()
                                    .username(user.getUsername())
                                    .password(encodedPassword)
                                    .nickname(user.getNickname())
                                    .name(user.getName())
                                    .age(user.getAge())
                                    .role("USER")
                                    .tel(user.getTel())
                                    .gender(user.getGender())
                                    .enabled(true)
                                    .score(36.5)
                                    .build();

                            return userRepository.save(newUser)
                                    .flatMap(savedUser ->
                                            userThumbnailService.uploadThumbnail(savedUser, thumbnails)
                                                    .flatMap(thumbnailIds -> {
                                                        String imgId = thumbnailIds.isEmpty() ? null : thumbnailIds.get(0);
                                                        savedUser.setImgId(imgId);

                                                        return userRepository.save(savedUser);
                                                    })
                                    );
                        })
                );
    }




    @Override
    public Mono<String> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> new BCryptPasswordEncoder().matches(password, user.getPassword()))
                .flatMap(user -> tokenService.createAndSaveToken(user.getId()));
    }
}

