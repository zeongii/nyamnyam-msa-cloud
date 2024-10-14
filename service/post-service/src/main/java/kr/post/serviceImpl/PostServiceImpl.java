package kr.post.serviceImpl;

import com.querydsl.core.Tuple;
import jakarta.transaction.Transactional;
import kr.post.absent.Pagination;
import kr.post.absent.Chart.UserPostModel;
import kr.post.component.ImageModel;
import kr.post.component.PostModel;
import kr.post.entity.*;
import kr.post.repository.PostRepository;
import kr.post.repository.PostTagRepository;
import kr.post.repository.RestaurantRepository;
import kr.post.repository.TagRepository;
import kr.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository repository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public double allAverageRating(Long restaurantId) {
        List<PostEntity> posts = repository.findByRestaurantId(restaurantId);

        if (posts.isEmpty()) {
            return 0.0;
        }

        double totalRating = posts.stream()
                .mapToDouble(post -> {
                    double averageRating = (post.getTaste() + post.getClean() + post.getService()) / 3.0;
                    return Math.min(averageRating, 5.0);
                })
                .sum();

        return totalRating / posts.size();
    }

    @Override
    public PostModel postWithImage(Long postId) {
        PostEntity postEntity = repository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        List<ImageEntity> images = postEntity.getImages();
        Tuple postWithNickname = repository.findPostWithNicknameById(postId);
        String nickname = postEntity.getNickname() != null ? postEntity.getNickname() : "닉네임 없음";

        PostModel postModel = convertToModelWithNickname(postEntity);
        postModel.setImages(postEntity.getImages().stream()
                .map(image -> ImageModel.builder()
                        .id(image.getId())
                        .originalFilename(image.getOriginalFileName())
                        .storedFileName(image.getStoredFileName())
                        .extension(image.getExtension())
                        .uploadURL(image.getUploadURL())
                        .build())
                .collect(Collectors.toList()));

        return postModel;
    }

    @Override
    public PostEntity findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    @Override
    public List<PostModel> findAllPerPage(int page) {
        Long totalCount = count();
        Pagination p = new Pagination(page, totalCount.intValue());
        return repository.findAll().stream()
                .skip(p.getStartRow())
                .limit(p.getEndRow() - p.getStartRow() + 1)
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Boolean crawling() {
        String url = "http://www.cgv.co.kr/movies/";
        Document doc = null;
        List<PostEntity> posts = new ArrayList<>();

        try {
            doc = Jsoup.connect(url).timeout(5000).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (doc == null) {
            System.out.println("문서를 로드할 수 없습니다.");
        }

        Elements elements = doc.select("div.sect-movie-chart");
        Iterator<Element> rank = elements.select("strong.rank").iterator();
        Iterator<Element> title = elements.select("strong.title").iterator();

        while (rank.hasNext() && title.hasNext()) {
            String movieRank = rank.next().text().replaceAll("[^0-9]", "");
            String movieTitle = title.next().text();
            System.out.println(movieRank + ": " + movieTitle);

            PostEntity post = PostEntity.builder()
                    .content("Rank: " + movieRank + ", Title: " + movieTitle)
                    .taste(Long.parseLong(movieRank)) // 맛을 순위로 대체, 예시로 사용
                    .clean(5L) // 예시 값
                    .service(5L) // 예시 값
                    .build();

            posts.add(post);
            repository.saveAll(posts);
            repository.findAll();
        }
        return repository.findAll().isEmpty();
    }

    @Override
    public List<PostModel> findAllByRestaurant(Long restaurantId) {
        List<PostEntity> allByRestaurantWithNickname = repository.findAllByRestaurantWithNickname(restaurantId);
        return allByRestaurantWithNickname.stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    private PostModel convertToModelWithNickname(PostEntity postEntity) {
        PostModel postModel = convertToModel(postEntity);
        //postModel.setNickname(nickname);
        postModel.setRestaurantId(postEntity.getRestaurant().getId());
        return postModel;
    }

    @Override
    public PostModel findById(Long id) {
        return repository.findById(id)
                .map(this::convertToModel)
                .orElse(null);
    }

    @Override
    public Boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Long count() {
        return repository.count();
    }

    @Transactional
    @Override
    public Boolean deleteById(Long id) {
        if (existsById(id)) {
            postTagRepository.deleteByPostId(id);
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public Long createPostWithImages(PostModel model) {
        PostEntity entity = convertToEntity(model);
        entity.setEntryDate(LocalDateTime.now());
        repository.save(entity); // Post 저장

        saveTags(model.getTags(), entity); // Tag 저장

        return entity.getId();
    }

    @Transactional
    @Override
    public Long createPost(PostModel model) {
        PostEntity entity = convertToEntity(model);
        entity.setEntryDate(LocalDateTime.now());
        repository.save(entity);

        saveTags(model.getTags(), entity);

        return entity.getId();
    }
    @Transactional
    @Override
    public Long updatePost(PostModel model) {
        PostEntity existingEntity = repository.findById(model.getId())
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + model.getId()));

        existingEntity.setContent(model.getContent());
        existingEntity.setTaste(model.getTaste());
        existingEntity.setClean(model.getClean());
        existingEntity.setService(model.getService());
        existingEntity.setModifyDate(LocalDateTime.now());

        updateTags(model.getTags(), existingEntity);
        PostEntity updatedEntity = repository.save(existingEntity);

        return updatedEntity.getId();
    }

    @Override
    public List<UserPostModel> findByUserId(String userId) {
        List<UserPostModel> userPostModels = repository.findByUserId(userId);
        System.out.println(userPostModels);
        return repository.findByUserId(userId);
    }


    private void updateTags(List<String> tags, PostEntity postEntity) {
        List<PostTagEntity> existPostTags = postTagRepository.findByPost(postEntity);
        List<PostTagEntity> newPostTags = tags.stream()
                .filter(tagName -> existPostTags.stream().noneMatch(postTag -> postTag.getTag().getName().equals(tagName)))
                .map(tagName -> {
                    TagEntity tag = tagRepository.findByName(tagName)
                            .orElseGet(() -> {
                                TagEntity newTag = new TagEntity();
                                newTag.setName(tagName);
                                tagRepository.save(newTag);
                                return newTag;
                            });
                    return new PostTagEntity(postEntity, tag);
                })
                .collect(Collectors.toList());

        postTagRepository.saveAll(newPostTags);

        List<PostTagEntity> tagsToDelete = existPostTags.stream()
                .filter(postTag -> !tags.contains(postTag.getTag().getName()))
                .collect(Collectors.toList());

        postTagRepository.deleteAll(tagsToDelete);
    }

    private void saveTags(List<String> tags, PostEntity postEntity) {
        if (tags != null && !tags.isEmpty()) {
            List<PostTagEntity> postTags = tags.stream()
                    .map(tagName -> {
                        TagEntity tag = tagRepository.findByName(tagName)
                                .orElseGet(() -> {
                                    TagEntity newTag = new TagEntity();
                                    newTag.setName(tagName);
                                    tagRepository.save(newTag);
                                    return newTag;
                                });
                        return new PostTagEntity(postEntity, tag);
                    })
                    .collect(Collectors.toList());
            postTagRepository.saveAll(postTags);
        }
    }

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

    private PostEntity convertToEntity(PostModel model) {
        RestaurantEntity restaurant = restaurantRepository.findById(model.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + model.getRestaurantId()));

        return PostEntity.builder()
                .content(model.getContent())
                .taste(model.getTaste())
                .clean(model.getClean())
                .service(model.getService())
                .userId(model.getUserId())
                .nickname(model.getNickname())
                .restaurant(restaurant)
                .build();
    }
}
