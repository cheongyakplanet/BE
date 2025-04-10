package org.cheonyakplanet.be.domain.repository;

import java.util.List;

import org.cheonyakplanet.be.domain.entity.comunity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	void deletePostById(Long id);

	Post findPostById(Long id);

	@Query("select p from Post p order by p.likes desc ")
	List<Post> findPostsOrderByLikes(Pageable pageable);

	Page<Post> findAllByDeletedAtIsNull(Pageable pageable);

	Page<Post> findAllByDeletedAtIsNullAndIsBlindIsFalse(Pageable pageable);
}
