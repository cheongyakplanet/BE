package org.cheonyakplanet.be.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.cheonyakplanet.be.application.dto.community.PostDTO;
import org.cheonyakplanet.be.domain.Stamped;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
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

    @Builder.Default
    @JsonManagedReference
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    private boolean isBlind = false;

    public void incrementLikes() {
        this.likes++;
    }
    public void decrementLikes() {
        this.likes--;
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
                .createdAt(post.getCreatedAt())
                .build();
    }

}
