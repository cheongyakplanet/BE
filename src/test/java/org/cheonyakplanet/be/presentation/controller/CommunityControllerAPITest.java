package org.cheonyakplanet.be.presentation.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.cheonyakplanet.be.application.dto.community.CommentDTO;
import org.cheonyakplanet.be.application.dto.community.PostCreateDTO;
import org.cheonyakplanet.be.application.dto.community.PostDTO;
import org.cheonyakplanet.be.application.service.CommunityService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CommunityControllerAPITest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CommunityService communityService;

	private PostCreateDTO postCreateDTO;
	private CommentDTO commentDTO;
	private UserDetailsImpl userDetails;
	private User user;
	private Post post;
	private PostDTO postDTO;

	@BeforeEach
	void setUp() {
		postCreateDTO = new PostCreateDTO();
		postCreateDTO.setTitle("Test Title");
		postCreateDTO.setContent("Test Content");

		commentDTO = new CommentDTO();
		commentDTO.setContent("Test Comment");

		// 테스트용 User 객체 생성
		user = new User("test@test.com", "password", UserRoleEnum.USER, "tester");
		userDetails = new UserDetailsImpl(user);

		post = Post.builder()
			.id(1L)
			.title("Test Title")
			.content("Test Content")
			.username("tester")
			.views(0L)
			.likes(0)
			.build();

		postDTO = PostDTO.builder()
			.id(1L)
			.title("Test Title")
			.content("Test Content")
			.username("tester")
			.views(0L)
			.likes(0)
			.createdAt(LocalDateTime.now())
			.build();

		// SecurityContext 설정
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);
	}

	@Test
	@DisplayName("게시글 작성 테스트")
	void createPostTest() throws Exception {
		// given
		given(communityService.createPost(any(PostCreateDTO.class), any(UserDetailsImpl.class)))
			.willReturn(post);

		// when & then
		mockMvc.perform(post("/api/community/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(postCreateDTO))
				.with(user(userDetails))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").value("게시글 작성 완료"));

		verify(communityService).createPost(any(PostCreateDTO.class), any(UserDetailsImpl.class));
	}

	@Test
	@DisplayName("모든 게시글 조회 테스트 - 인증 없이")
	void getAllPostsWithoutAuthTest() throws Exception {
		// given
		List<PostDTO> postList = List.of(postDTO);
		Page<PostDTO> postPage = new PageImpl<>(postList, PageRequest.of(0, 10), 1);

		given(communityService.getAllPosts(anyString(), anyInt(), anyInt()))
			.willReturn(postPage);

		// when & then
		mockMvc.perform(get("/api/community/posts")
				.param("sort", "time")
				.param("page", "0")
				.param("size", "10"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("fail"))
			.andExpect(jsonPath("$.data.code").value("AUTH001"))
			.andExpect(jsonPath("$.data.message").value("인증이 필요한 서비스입니다"));

		// verify 부분 제거 - 컨트롤러까지 도달하지 못했으므로 서비스가 호출되지 않음
		verify(communityService, never()).getAllPosts(anyString(), anyInt(), anyInt());
	}

	@Test
	@DisplayName("모든 게시글 조회 테스트 - 인증 있음")
	void getAllPostsWithAuthTest() throws Exception {
		// given
		List<PostDTO> postList = List.of(postDTO);
		Page<PostDTO> postPage = new PageImpl<>(postList, PageRequest.of(0, 10), 1);

		given(communityService.getAllPosts(anyString(), anyInt(), anyInt()))
			.willReturn(postPage);

		// when & then
		mockMvc.perform(get("/api/community/posts")
				.param("sort", "time")
				.param("page", "0")
				.param("size", "10")
				.with(user(userDetails)))  // 인증 정보 추가
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(communityService).getAllPosts(anyString(), anyInt(), anyInt());
	}

	@Test
	@DisplayName("게시글 단건 조회 테스트")
	void getPostTest() throws Exception {
		// given
		given(communityService.getPostById(anyLong()))
			.willReturn(post);

		// when & then
		mockMvc.perform(get("/api/community/post/{id}", 1L)
				.with(user(userDetails)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").exists());

		verify(communityService).getPostById(anyLong());
	}

	@Test
	@DisplayName("게시글 삭제 테스트")
	void deletePostTest() throws Exception {
		// given
		doNothing().when(communityService).deletePost(anyLong(), any(UserDetailsImpl.class));

		// when & then
		mockMvc.perform(delete("/api/community/post/{id}", 1L)
				.with(user(userDetails))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").value("게시글 삭제 완료"));

		verify(communityService).deletePost(anyLong(), any(UserDetailsImpl.class));
	}

	@Test
	@DisplayName("게시글 좋아요 테스트")
	void likePostTest() throws Exception {
		// given
		doNothing().when(communityService).likePost(anyLong(), any(UserDetailsImpl.class));

		// when & then
		mockMvc.perform(post("/api/community/post/like/{id}", 1L)
				.with(user(userDetails))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").value("좋아요 +1"));

		verify(communityService).likePost(anyLong(), any(UserDetailsImpl.class));
	}

	@Test
	@DisplayName("게시글 싫어요 테스트")
	void dislikePostTest() throws Exception {
		// given
		doNothing().when(communityService).dislikePost(anyLong(), any(UserDetailsImpl.class));

		// when & then
		mockMvc.perform(post("/api/community/post/dislike/{id}", 1L)
				.with(user(userDetails))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").value("싫어요 +1"));

		verify(communityService).dislikePost(anyLong(), any(UserDetailsImpl.class));
	}

	@Test
	@DisplayName("내가 쓴 글 조회 테스트")
	void getMyPostsTest() throws Exception {
		// given
		List<PostDTO> myPostList = List.of(postDTO);
		Page<PostDTO> myPostPage = new PageImpl<>(myPostList, PageRequest.of(0, 10), 1);

		given(communityService.getMyPosts(anyString(), anyInt(), anyInt(), any(UserDetailsImpl.class)))
			.willReturn(myPostPage);

		// when & then
		mockMvc.perform(get("/api/community/post/my")
				.param("sort", "time")
				.param("page", "0")
				.param("size", "10")
				.with(user(userDetails)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(communityService).getMyPosts(anyString(), anyInt(), anyInt(), any(UserDetailsImpl.class));
	}

	@Test
	@DisplayName("댓글 작성 테스트")
	void addCommentTest() throws Exception {
		// given
		doNothing().when(communityService).addComment(anyLong(), any(CommentDTO.class), any(UserDetailsImpl.class));

		// when & then
		mockMvc.perform(post("/api/community/comment/{postId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(commentDTO))
				.with(user(userDetails))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").value("댓글 작성 완료"));

		verify(communityService).addComment(anyLong(), any(CommentDTO.class), any(UserDetailsImpl.class));
	}

	@Test
	@DisplayName("대댓글 작성 테스트")
	void addReplyTest() throws Exception {
		// given
		given(communityService.addReply(anyLong(), any(CommentDTO.class), any(UserDetailsImpl.class)))
			.willReturn(null);

		// when & then
		mockMvc.perform(post("/api/community/comment/comment/{commentId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(commentDTO))
				.with(user(userDetails))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").value("대댓글 작성 완료"));

		verify(communityService).addReply(anyLong(), any(CommentDTO.class), any(UserDetailsImpl.class));
	}
}