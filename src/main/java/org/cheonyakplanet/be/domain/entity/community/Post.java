package org.cheonyakplanet.be.domain.entity.community;

import java.util.ArrayList;
import java.util.List;

import org.cheonyakplanet.be.application.dto.community.PostDTO;
import org.cheonyakplanet.be.domain.Stamped;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(catalog = "planet", name = "Post")
public class Post extends Stamped {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;

	private String title;

	@Column(columnDefinition = "TEXT")
	private String content;

	private Long views; // 조회수

	private int likes; // 추천 수
	private int dislikes; //싫어요 수

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PostCategory category;

	@Builder.Default
	@JsonManagedReference
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments = new ArrayList<>();

	@Builder.Default
	private boolean isBlind = false;

	public void incrementLikes() {
		this.likes++;
	}

	public void increaseDislikes() {
		this.dislikes++;
	}

	public void countViews() {
		this.views = (this.views == null ? 1L : this.views + 1);
	}

	public static PostDTO ToDTO(Post post) {
		return PostDTO.builder()
			.id(post.getId())
			.username(post.getUsername())
			.title(post.getTitle())
			.content(post.getContent())
			.views(post.getViews())
			.likes(post.getLikes())
			.category(post.getCategory())
			.createdAt(post.getCreatedAt())
			.updatedAt(post.getUpdatedAt())
			.build();
	}

	public void updateContent(String newTitle, String newContent, PostCategory newCategory) {
		this.title    = newTitle;
		this.content  = newContent;
		this.category = newCategory;
	}
}