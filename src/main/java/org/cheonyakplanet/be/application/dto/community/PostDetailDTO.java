package org.cheonyakplanet.be.application.dto.community;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.cheonyakplanet.be.domain.entity.comunity.Post;
import org.cheonyakplanet.be.domain.entity.comunity.PostReaction;
import org.cheonyakplanet.be.domain.entity.comunity.ReactionType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostDetailDTO {
	private Long id;
	private String username;
	private String title;
	private String content;
	private long views;
	private int likes;
	private int dislikes;
	private LocalDateTime createdAt;
	private List<CommentDTO> comments;
	private ReactionType myReaction;

	public static PostDetailDTO fromEntity(Post post, PostReaction myReaction) {
		return PostDetailDTO.builder()
			.id(post.getId())
			.username(post.getUsername())
			.title(post.getTitle())
			.content(post.getContent())
			.views(post.getViews())
			.likes(post.getLikes())
			.dislikes(post.getDislikes())
			.createdAt(post.getCreatedAt())
			.comments(post.getComments().stream()
				.map(CommentDTO::fromEntity)
				.collect(Collectors.toList()))
			.myReaction(myReaction != null ? myReaction.getReactionType() : null)
			.build();
	}
}
