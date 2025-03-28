package org.cheonyakplanet.be.domain.repository;

import org.cheonyakplanet.be.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    void deletePostById(Long id);

    Post findPostById(Long id);

    @Query("select p from Post p order by p.likes desc ")
    List<Post> findPostsOrderByLikes(Pageable pageable);

    Page<Post> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Post> findAllByDeletedAtIsNullAndIsBlindIsFalse(Pageable pageable);
}
