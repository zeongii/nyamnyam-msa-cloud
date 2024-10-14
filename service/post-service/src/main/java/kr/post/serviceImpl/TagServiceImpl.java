package kr.post.serviceImpl;

import jakarta.persistence.EntityNotFoundException;
import kr.post.component.TagModel;
import kr.post.entity.PostEntity;
import kr.post.entity.PostTagEntity;
import kr.post.entity.TagCategory;
import kr.post.entity.TagEntity;
import kr.post.repository.PostRepository;
import kr.post.repository.TagRepository;
import kr.post.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository repository;
    private final PostRepository postRepository;

    // 레스토랑 별 Top5 Tag
    @Override
    public List<String> getTagRestaurant(Long restaurantId) {
        List<PostEntity> posts = postRepository.findByRestaurantId(restaurantId);
        Map<String , Long> tagCount = new HashMap<>();

        for (PostEntity post : posts) {
            for (PostTagEntity postTag : post.getPostTags()) {
                String tagName = postTag.getTag().getName();
                tagCount.put(tagName, tagCount.getOrDefault(tagName, 0L) +1);
            }
        }
        return tagCount.entrySet().stream()
                .sorted((t1, t2) -> t2.getValue().compareTo(t1.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getTagCategory() {
        return Arrays.stream(TagCategory.values())
                .map(TagCategory::getDisplayName)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<TagModel>> getTagsByCategory() {
        List<TagEntity> tags = repository.findAll();
        return tags.stream()
                .map(this::convertToModel)
                .collect(Collectors.groupingBy(tag -> tag.getTagCategory().getDisplayName()));
    }

    @Override
    public List<TagModel> findAll() {
        return repository.findAll().stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TagModel> findByName(String name) {
        return repository.findByName(name)
                .map(this::convertToModel);
    }

    @Override
    public Boolean existsByName(String name) {
        return repository.existsById(name);
    }

    @Override
    public Boolean deleteByName(String name) {
        try {
            repository.deleteById(name);
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }

    @Override
    public Boolean save(TagModel model) {
        TagEntity entity = convertToEntity(model);
        return repository.save(entity) != null;
    }

    private TagModel convertToModel(TagEntity entity){
        return TagModel.builder()
                .name(entity.getName())
                .tagCategory(entity.getTagCategory())
                .build();
    }

    private TagEntity convertToEntity(TagModel model){
        return new TagEntity(model.getName(), model.getTagCategory());
    }
}
