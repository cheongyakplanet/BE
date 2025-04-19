package org.cheonyakplanet.be.presentation.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.cheonyakplanet.be.application.service.CommunityService;
import org.cheonyakplanet.be.application.service.SubscriptionService;
import org.cheonyakplanet.be.domain.entity.comunity.Post;
import org.cheonyakplanet.be.domain.entity.user.User;
import org.cheonyakplanet.be.domain.entity.user.UserRoleEnum;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class HomeControllerAPITest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SubscriptionService subscriptionService;

	@MockBean
	private CommunityService communityService;

	private UserDetailsImpl userDetails;
	private User user;

	@BeforeEach
	void setUp() {
		// 테스트용 User 객체 생성
		user = new User("test@test.com", "password", UserRoleEnum.USER, "tester");
		userDetails = new UserDetailsImpl(user);

		// SecurityContext 설정
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);
	}

	@Test
	@DisplayName("가장 인기 있는 지역 조회 테스트")
	void getPopularLocationsTest() throws Exception {
		// given
		List<String> popularLocations = Arrays.asList("서울특별시 동대문구", "서울 강동구");
		given(subscriptionService.getPopularLocationList()).willReturn(popularLocations);

		// when & then
		mockMvc.perform(get("/api/main/popular-locations"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0]").value("서울특별시 동대문구"))
			.andExpect(jsonPath("$.data[1]").value("서울 강동구"));

		verify(subscriptionService).getPopularLocationList();
	}

	@Test
	@DisplayName("내 관심 지역 조회 테스트 - 인증 있음")
	void getMyLocationsWithAuthTest() throws Exception {
		// given
		List<String> interestLocals = Arrays.asList(
			"서울특별시 동대문구",
			"서울특별시 서대문구",
			"서울특별시 서초구",
			"서울특별시 강북구",
			"서울시 용산구"
		);
		given(subscriptionService.getInterestLocalsByEmail(any())).willReturn(interestLocals);

		// when & then
		mockMvc.perform(get("/api/main/my-locations")
				.with(user(userDetails)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0]").value("서울특별시 동대문구"))
			.andExpect(jsonPath("$.data[4]").value("서울시 용산구"));

		verify(subscriptionService).getInterestLocalsByEmail(any());
	}

	// @Test
	// @DisplayName("내 관심 지역 조회 테스트 - 인증 없음")
	// void getMyLocationsWithoutAuthTest() throws Exception {
	// 	// when & then
	// 	mockMvc.perform(get("/api/main/my-locations"))
	// 		.andDo(print())
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("fail"))
	// 		.andExpect(jsonPath("$.data.code").value("AUTH001"))
	// 		.andExpect(jsonPath("$.data.message").value("인증이 필요한 서비스입니다"));
	//
	// 	verify(subscriptionService, never()).getInterestLocalsByEmail(any());
	// }

	@Test
	@DisplayName("인기 있는 게시글 조회 테스트")
	void getPopularPostsTest() throws Exception {
		// given
		Post post = Post.builder()
			.id(6L)
			.username("tester")
			.title("예제 제목")
			.content("예제 내용")
			.views(506L)
			.likes(80)
			.comments(new java.util.ArrayList<>())
			.build();
		post.setCreatedAt(LocalDateTime.of(2025, 2, 15, 11, 49, 13));
		post.setUpdatedAt(LocalDateTime.of(2025, 3, 26, 8, 59, 46));

		List<Post> popularPosts = Arrays.asList(post);
		given(communityService.getPopularPosts()).willReturn(popularPosts);

		// when & then
		mockMvc.perform(get("/api/main/popular-content"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].id").value(6))
			.andExpect(jsonPath("$.data[0].username").value("tester"))
			.andExpect(jsonPath("$.data[0].title").value("예제 제목"))
			.andExpect(jsonPath("$.data[0].content").value("예제 내용"))
			.andExpect(jsonPath("$.data[0].views").value(506))
			.andExpect(jsonPath("$.data[0].likes").value(80));

		verify(communityService).getPopularPosts();
	}
}