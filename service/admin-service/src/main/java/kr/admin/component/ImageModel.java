package kr.admin.component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageModel {
    private Long id;
    private String originalFilename;
    private String storedFileName;
    private String extension;
    private String uploadPath;
    private String uploadURL;
    private Long postId;
}
