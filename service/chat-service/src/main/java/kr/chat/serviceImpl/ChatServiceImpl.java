package kr.chat.serviceImpl;


import com.amazonaws.services.s3.AmazonS3;
import kr.chat.document.Chat;
import kr.chat.repository.ChatRepository;
import kr.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final AmazonS3 amazonS3;
    @Value("${naver.storage.bucket}")
    private String bucketName;

    @Value("${naver.storage.upload.path}")
    private String uploadPath;

    // 파일 업로드 메서드
    public Mono<String> uploadFile(MultipartFile file) {
        return Mono.fromCallable(() -> {
            String fileName = file.getOriginalFilename();
            String extension = fileName.substring(fileName.lastIndexOf("."));
            String uploadName = UUID.randomUUID() + extension;

            // 파일을 네이버 클라우드 스토리지에 업로드
            try {
                amazonS3.putObject(bucketName, uploadPath + "/" + uploadName, file.getInputStream(), null);
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 중 오류 발생", e);
            }

            // 업로드된 파일의 URL 반환
            return String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s/%s", bucketName, uploadPath, uploadName);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Chat> mFindBySender(String sender, String chatRoomId) {
        return chatRepository.mFindBySender(sender, chatRoomId)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Chat> mFindByChatRoomId(String chatRoomId) {
        return chatRepository.mFindByChannelId(chatRoomId)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Chat> saveMessage(Chat chat) {

        return chatRepository.save(chat);
    }


    @Override
    public Mono<Long> getUnreadMessageCountByChatRoomId(String chatRoomId, String nickname) {
        return chatRepository.findByChatRoomId(chatRoomId)
                .sort((chat1, chat2) -> chat2.getCreatedAt().compareTo(chat1.getCreatedAt())) // 최신 메시지 우선 정렬
                .flatMap(chat -> {
                    Map<String, Boolean> readBy = chat.getReadBy();
                    // readBy가 null이면 빈 맵으로 초기화
                    if (readBy == null) {
                        readBy = Map.of();
                    }
                    // 읽지 않은 메시지 체크
                    return Mono.just(!Boolean.TRUE.equals(readBy.get(nickname)));
                })
                .takeUntil(isRead -> !isRead) // 읽지 않은 메시지를 찾을 때까지 반복
                .count() // 읽지 않은 메시지 수를 세어 반환
                .map(count -> count-1L);
    }


    @Override
    public Mono<Long> getParticipantsNotReadCount(String chatId) {
        return chatRepository.findById(chatId)
                .map(chat -> {
                    long totalParticipants = chat.getTotalParticipants() != null ? chat.getTotalParticipants() : 0; // 총 참가자 수
                    long readCount = chat.getReadBy().values().stream()
                            .filter(Boolean::booleanValue) // 읽은 참가자 수
                            .count();
                    return totalParticipants - readCount; // 전체 참가자 수에서 읽은 수를 뺌
                });
    }


    @Override
    public Mono<Chat> markAsRead(String chatId, String nickname) {
        return chatRepository.findById(chatId)
                .flatMap(chat -> {
                    if (chat.getReadBy() == null) {
                        chat.setReadBy(new HashMap<>()); // readBy 초기화
                    }
                    chat.getReadBy().put(nickname, true); // 닉네임을 읽음으로 설정
                    return chatRepository.save(chat); // 업데이트된 메시지 저장
                });
    }

    //기존 메시지에 readBy값 추가
    @Override
    public Mono<Chat> updateReadBy(String chatId, String nickname) {
        return chatRepository.findById(chatId)
                .flatMap(chat -> {
                    if (chat.getReadBy() == null) {
                        chat.setReadBy(new HashMap<>()); // readBy 초기화
                    }
                    // 특정 사용자의 읽음 상태를 업데이트
                    chat.getReadBy().put(nickname, true); // 닉네임을 읽음으로 설정
                    return chatRepository.save(chat); // 업데이트된 메시지 저장
                });
    }
}