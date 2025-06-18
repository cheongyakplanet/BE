package org.cheonyakplanet.be.domain.repository;

import org.cheonyakplanet.be.domain.entity.comunity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
