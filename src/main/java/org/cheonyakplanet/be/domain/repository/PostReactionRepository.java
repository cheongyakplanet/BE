package org.cheonyakplanet.be.domain.repository;

import java.util.Optional;

import org.cheonyakplanet.be.domain.entity.community.Post;
import org.cheonyakplanet.be.domain.entity.community.PostReaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

	Optional<PostReaction> findByPostAndEmail(Post post, String email);
}
