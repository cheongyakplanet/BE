package org.cheonyakplanet.be.application.dto.community;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

import org.cheonyakplanet.be.domain.entity.comunity.PostCategory;

@Builder
@Getter @Setter
@AllArgsConstructor
public class PostDTO {

    @Schema(description = "게시글 id", example = "")
    private Long id;

    @Schema(description = "게시글 제목", example = "예제 제목")
    private String title;

    @Schema(description = "게시글 내용", example = "예제 내용")
    private String content;

    @Schema(description = "게시글 작성자", example = "tester")
    private String username;

    private Long views;

    private int likes;

    private PostCategory category;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
