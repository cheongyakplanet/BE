package org.cheonyakplanet.be.application.dto.community;

import org.cheonyakplanet.be.domain.entity.community.PostCategory;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostUpdateRequestDTO(
	@Schema(description = "게시글 제목", example = "제목 수정")
	String title,
	@Schema(description = "게시글 내용", example = "내용 수정")
	String content,
	@Schema(description = "카테고리", example = "변경하고 싶은 카테고리 선택")
	PostCategory category
) {
}

