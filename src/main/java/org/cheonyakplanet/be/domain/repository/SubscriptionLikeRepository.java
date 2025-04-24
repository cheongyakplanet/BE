package org.cheonyakplanet.be.domain.repository;

import java.util.List;

import org.cheonyakplanet.be.domain.entity.SubscriptionLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionLikeRepository extends JpaRepository<SubscriptionLike, Long> {

	List<SubscriptionLike> findByCreatedBy(String createdBy);
}
