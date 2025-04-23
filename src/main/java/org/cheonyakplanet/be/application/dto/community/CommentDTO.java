package org.cheonyakplanet.be.application.dto.community;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.cheonyakplanet.be.domain.entity.comunity.Comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

	private Long id;

	@Schema(example = "댓글 내용")
	private String content;

	private String username;

	private List<ReplyDTO> replies;

	private LocalDateTime createdAt;

	public static CommentDTO fromEntity(Comment comment) {
		return CommentDTO.builder()
			.id(comment.getId())
			.content(comment.getContent())
			.replies(comment.getReplies().stream()
				.map(ReplyDTO::fromEntity)
				.collect(Collectors.toList()))
			.username(comment.getUsername())
			.createdAt(comment.getCreatedAt())
			.build();
	}
}
