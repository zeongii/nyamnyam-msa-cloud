package kr.gateway.service;


import kr.gateway.document.User;
import kr.gateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    public Mono<Boolean> existsById(String id) {
        return userRepository.existsById(id);
    }

    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Mono<User> findById(String id) {
        return userRepository.findById(id);
    }


    public Mono<String> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> new BCryptPasswordEncoder().matches(password, user.getPassword()))
                .flatMap(user -> tokenService.createAndSaveToken(user.getId()));
    }
}

