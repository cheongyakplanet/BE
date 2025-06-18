package org.cheonyakplanet.be.domain.repository;

import org.cheonyakplanet.be.domain.entity.comunity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
