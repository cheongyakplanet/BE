package org.cheonyakplanet.be.application.dto.user;

public record UserUpdateResponseDTO(
	UserDTO user,
	String token
) {
}
