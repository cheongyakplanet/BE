package org.cheonyakplanet.be.domain;

import java.time.LocalDateTime;

import org.cheonyakplanet.be.domain.entity.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.persistence.PreUpdate;

public class SoftDeleteListener {
	/**
	 * 엔티티 업데이트 전에 호출됩니다.
	 * 여기서 삭제 상태("DELETED")로 변경된 경우, deletedAt과 deletedBy를 설정합니다.
	 */
	@PreUpdate
	public void preUpdate(Stamped entity) {
		// 예시: Member 엔티티에서 status가 "DELETED"로 변경된 경우 처리
		if (entity instanceof User) {
			User user = (User)entity;
			// 아직 deletedAt가 설정되지 않았고, 상태가 DELETED라면 소프트 삭제 처리
			if (user.getDeletedAt() == null && "DELETED".equals(user.getStatus())) {
				user.setDeletedAt(LocalDateTime.now());

				// 현재 인증된 사용자 정보를 SecurityContextHolder에서 가져옵니다.
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				if (auth != null && auth.isAuthenticated()) {
					user.setDeletedBy(auth.getName());
				} else {
					user.setDeletedBy("SYSTEM"); // 인증 정보가 없을 경우
				}
			}
		}
	}
}
