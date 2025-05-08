package org.cheonyakplanet.be.application.dto.community;

import java.time.LocalDateTime;

import org.cheonyakplanet.be.domain.entity.comunity.Reply;

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
public class ReplyDTO {
	private Long id;
	private String content;
	private String username;
	private LocalDateTime createdAt;

	public static ReplyDTO fromEntity(Reply reply) {
		return ReplyDTO.builder()
			.id(reply.getId())
			.content(reply.getContent())
			.username(reply.getUsername())
			.createdAt(reply.getCreatedAt())
			.build();
	}
}
