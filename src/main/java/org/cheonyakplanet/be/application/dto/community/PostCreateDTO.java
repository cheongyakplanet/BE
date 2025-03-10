package org.cheonyakplanet.be.application.dto.community;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateDTO {
    @Schema(description = "게시글 제목", example = "예제 제목")
    private String title;

    @Schema(description = "게시글 내용", example = "예제 내용")
    private String content;
}
