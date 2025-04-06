package org.cheonyakplanet.be.domain.entity.user;

public enum UserRoleEnum {

	USER(Authority.User),
	ADMIN(Authority.ADMIN);

	private final String authority;

	UserRoleEnum(String authority) {
		this.authority = authority;
	}

	public String getAuthority() {
		return this.authority;
	}

	public static class Authority {
		public static final String User = "ROLE_USER";
		public static final String ADMIN = "ROLE_ADMIN";
	}
}
