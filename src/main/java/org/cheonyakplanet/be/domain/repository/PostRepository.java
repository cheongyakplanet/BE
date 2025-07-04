package org.cheonyakplanet.be.domain.repository;

import java.util.List;
import java.util.Optional;

import org.cheonyakplanet.be.domain.entity.community.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	// TODO : 조회에서 빼지 않고 "삭제된 게시글입니다."로 보이는 것 검토
	Post findPostByIdAndDeletedAtIsNull(Long id);

	@Query("select p from Post p order by p.likes desc ")
	List<Post> findPostsOrderByLikesAndDeletedAtIsNull(Pageable pageable);

	Page<Post> findAllByDeletedAtIsNullAndIsBlindIsFalse(Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.username = :username AND p.deletedAt IS NULL AND p.isBlind = false")
	Page<Post> findByUsernameAndDeletedAtIsNullAndBlindIsFalse(@Param("username") String username, Pageable pageable);

	@Modifying
	@Query("UPDATE Post p SET p.username = :newUsername WHERE p.username = :oldUsername")
	int updateUsernameForPosts(
		@Param("oldUsername") String oldUsername,
		@Param("newUsername") String newUsername
	);

	@Query("SELECT p FROM Post p WHERE p.id = :id AND p.deletedAt IS NULL AND p.isBlind = false")
	Optional<Post> findByIdAndDeletedAtIsNullAndBlindIsFalse(Long id);

	@Query("SELECT p FROM Post p WHERE p.title LIKE %:titleFragment% AND p.username = :username AND p.deletedAt IS NULL")
	List<Post> findByTitleContainingAndUsernameAndDeletedAtIsNull(@Param("titleFragment") String titleFragment, @Param("username") String username);
}
