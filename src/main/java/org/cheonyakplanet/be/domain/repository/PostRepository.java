package org.cheonyakplanet.be.domain.repository;

import java.util.List;

import org.cheonyakplanet.be.domain.entity.comunity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	Post findPostByIdAndDeletedAtIsNull(Long id);

	@Query("select p from Post p order by p.likes desc ")
	List<Post> findPostsOrderByLikesAndDeletedAtIsNull(Pageable pageable);

	Page<Post> findAllByDeletedAtIsNullAndIsBlindIsFalse(Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.username = :username AND p.deletedAt IS NULL AND p.isBlind = false")
	Page<Post> findByUsernameAndDeletedAtIsNullAndBlindIsFalse(@Param("username") String username, Pageable pageable);
}
