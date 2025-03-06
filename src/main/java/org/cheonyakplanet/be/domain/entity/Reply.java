package org.cheonyakplanet.be.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.cheonyakplanet.be.domain.Stamped;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(catalog = "planet", name = "Reply")
public class Reply extends Stamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
