package org.cheonyakplanet.be.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.cheonyakplanet.be.application.dto.community.CommentDTO;
import org.cheonyakplanet.be.application.dto.community.PostCreateDTO;
import org.cheonyakplanet.be.application.dto.community.PostDTO;
import org.cheonyakplanet.be.domain.entity.comunity.Comment;
import org.cheonyakplanet.be.domain.entity.comunity.Post;
import org.cheonyakplanet.be.domain.entity.comunity.PostReaction;
import org.cheonyakplanet.be.domain.entity.comunity.ReactionType;
import org.cheonyakplanet.be.domain.entity.comunity.Reply;
import org.cheonyakplanet.be.domain.entity.user.User;
import org.cheonyakplanet.be.domain.entity.user.UserRoleEnum;
import org.cheonyakplanet.be.domain.repository.CommentRepository;
import org.cheonyakplanet.be.domain.repository.PostReactionRepository;
import org.cheonyakplanet.be.domain.repository.PostRepository;
import org.cheonyakplanet.be.domain.repository.ReplyRepository;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class CommunityServiceTest {

	@Mock
	private PostRepository postRepository;

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private ReplyRepository replyRepository;

	@Mock
	private PostReactionRepository reactionRepository;

	@InjectMocks
	private CommunityService communityService;

	private User testUser;
	private UserDetailsImpl userDetails;
	private Post testPost;
	private PostCreateDTO postCreateDTO;
	private Comment testComment;
	private Reply testReply;

	@BeforeEach
	void setUp() {
		// 테스트용 사용자 설정
		testUser = new User("test@test", "password", UserRoleEnum.USER, "tester");
		userDetails = new UserDetailsImpl(testUser);

		// 테스트용 게시글 DTO 설정
		postCreateDTO = new PostCreateDTO();
		postCreateDTO.setTitle("테스트 제목");
		postCreateDTO.setContent("테스트 내용");

		// 테스트용 게시글 설정
		testPost = Post.builder()
			.id(1L)
			.username("tester")
			.title("테스트 제목")
			.content("테스트 내용")
			.likes(0)
			.views(0L)
			.build();

		// 테스트용 댓글 설정
		testComment = Comment.builder()
			.id(1L)
			.content("테스트 댓글")
			.post(testPost)
			.build();

		// 테스트용 대댓글 설정
		testReply = Reply.builder()
			.id(1L)
			.content("테스트 대댓글")
			.comment(testComment)
			.build();
	}

	@Test
	@DisplayName("게시글 작성 - 성공")
	void createPost_Success() {
		// given
		when(postRepository.save(any(Post.class))).thenReturn(testPost);

		// when
		Post result = communityService.createPost(postCreateDTO, userDetails);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTitle()).isEqualTo("테스트 제목");
		assertThat(result.getContent()).isEqualTo("테스트 내용");
		assertThat(result.getUsername()).isEqualTo("tester");
		assertThat(result.getLikes()).isEqualTo(0);
		verify(postRepository, times(1)).save(any(Post.class));
	}

	@Test
	@DisplayName("게시글 삭제 - 성공")
	void deletePost_Success() {
		// given
		Long postId = 1L;
		when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

		// when
		communityService.deletePost(postId, userDetails);

		// then
		verify(postRepository, times(1)).findById(postId);
		verify(postRepository, times(1)).delete(testPost);
	}

	@Test
	@DisplayName("게시글 삭제 - 실패: 게시글이 존재하지 않음")
	void deletePost_NotFound() {
		// given
		Long postId = 1L;
		when(postRepository.findById(postId)).thenReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			communityService.deletePost(postId, userDetails);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMU001);
		verify(postRepository, times(1)).findById(postId);
		verify(postRepository, never()).delete(any());
	}

	@Test
	@DisplayName("게시글 삭제 - 실패: 권한 없음")
	void deletePost_UnauthorizedUser() {
		// given
		Long postId = 1L;
		Post otherUserPost = Post.builder()
			.id(postId)
			.username("otherUser")
			.title("다른 사용자의 게시글")
			.content("내용")
			.build();

		when(postRepository.findById(postId)).thenReturn(Optional.of(otherUserPost));

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			communityService.deletePost(postId, userDetails);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMU002);
		verify(postRepository, times(1)).findById(postId);
		verify(postRepository, never()).delete(any());
	}

	@Test
	@DisplayName("게시글 목록 조회 - 최신순 정렬")
	void getAllPosts_SortByTime() {
		// given
		String sort = "time";
		int page = 0;
		int size = 10;
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

		List<Post> posts = List.of(testPost);
		Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

		when(postRepository.findAllByDeletedAtIsNullAndIsBlindIsFalse(pageable)).thenReturn(postPage);

		// when
		Page<PostDTO> result = communityService.getAllPosts(sort, page, size);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 제목");
		verify(postRepository, times(1)).findAllByDeletedAtIsNullAndIsBlindIsFalse(pageable);
	}

	// @Test
	// @DisplayName("게시글 상세 조회 - 성공")
	// void getPostById_Success() {
	// 	// given
	// 	Long postId = 1L;
	// 	when(postRepository.findPostByIdAndDeletedAtIsNull(postId)).thenReturn(testPost);
	//
	// 	// when
	// 	Post result = communityService.getPostById(postId);
	//
	// 	// then
	// 	assertThat(result).isNotNull();
	// 	assertThat(result.getId()).isEqualTo(postId);
	// 	assertThat(result.getViews()).isEqualTo(1L); // views가 증가했는지 확인
	// 	verify(postRepository, times(1)).findPostByIdAndDeletedAtIsNull(postId);
	// }

	// @Test
	// @DisplayName("게시글 상세 조회 - 실패: 게시글이 존재하지 않음")
	// void getPostById_NotFound() {
	// 	// given
	// 	Long postId = 1L;
	// 	when(postRepository.findPostByIdAndDeletedAtIsNull(postId)).thenReturn(null);
	//
	// 	// when & then
	// 	assertThrows(CustomException.class, () -> {
	// 		communityService.getPostById(postId);
	// 	});
	// 	verify(postRepository, times(1)).findPostByIdAndDeletedAtIsNull(postId);
	// }

	@Test
	@DisplayName("인기 게시글 조회 - 성공")
	void getPopularPosts_Success() {
		// given
		Pageable pageable = PageRequest.of(0, 5);
		List<Post> popularPosts = List.of(testPost);
		when(postRepository.findPostsOrderByLikesAndDeletedAtIsNull(pageable)).thenReturn(popularPosts);

		// when
		List<Post> result = communityService.getPopularPosts();

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0)).isEqualTo(testPost);
		verify(postRepository, times(1)).findPostsOrderByLikesAndDeletedAtIsNull(pageable);
	}

	@Test
	@DisplayName("내가 쓴 글 조회 - 성공")
	void getMyPosts_Success() {
		// given
		String sort = "likes";
		int page = 0;
		int size = 10;
		Pageable pageable = PageRequest.of(page, size, Sort.by("likes").descending());

		List<Post> myPosts = List.of(testPost);
		Page<Post> postPage = new PageImpl<>(myPosts, pageable, myPosts.size());

		when(postRepository.findByUsernameAndDeletedAtIsNullAndBlindIsFalse("tester", pageable))
			.thenReturn(postPage);

		// when
		Page<PostDTO> result = communityService.getMyPosts(sort, page, size, userDetails);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getUsername()).isEqualTo("tester");
		verify(postRepository, times(1))
			.findByUsernameAndDeletedAtIsNullAndBlindIsFalse("tester", pageable);
	}

	@Test
	@DisplayName("게시글 좋아요 - 성공")
	void likePost_Success() {
		// given
		Long postId = 1L;
		when(postRepository.findPostByIdAndDeletedAtIsNull(postId)).thenReturn(testPost);
		when(reactionRepository.findByPostAndEmail(testPost, userDetails.getUsername()))
			.thenReturn(Optional.empty());

		// when
		communityService.likePost(postId, userDetails);

		// then
		assertThat(testPost.getLikes()).isEqualTo(1);
		verify(postRepository, times(1)).findPostByIdAndDeletedAtIsNull(postId);
		verify(reactionRepository, times(1)).findByPostAndEmail(testPost, userDetails.getUsername());
		verify(reactionRepository, times(1)).save(any(PostReaction.class));
		verify(postRepository, times(1)).save(testPost);
	}

	@Test
	@DisplayName("게시글 좋아요 - 실패: 이미 반응한 게시글")
	void likePost_AlreadyReacted() {
		// given
		Long postId = 1L;
		when(postRepository.findPostByIdAndDeletedAtIsNull(postId)).thenReturn(testPost);

		PostReaction existingReaction = PostReaction.builder()
			.post(testPost)
			.email(userDetails.getUsername())
			.reactionType(ReactionType.LIKE)
			.build();

		when(reactionRepository.findByPostAndEmail(testPost, userDetails.getUsername()))
			.thenReturn(Optional.of(existingReaction));

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			communityService.likePost(postId, userDetails);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMU003);
		verify(postRepository, times(1)).findPostByIdAndDeletedAtIsNull(postId);
		verify(reactionRepository, times(1)).findByPostAndEmail(testPost, userDetails.getUsername());
		verify(reactionRepository, never()).save(any());
		verify(postRepository, never()).save(any());
	}

	@Test
	@DisplayName("댓글 작성 - 성공")
	void addComment_Success() {
		// given
		Long postId = 1L;
		CommentDTO commentDTO = new CommentDTO();
		commentDTO.setContent("테스트 댓글");

		when(postRepository.findPostByIdAndDeletedAtIsNull(postId)).thenReturn(testPost);

		// when
		communityService.addComment(postId, commentDTO, userDetails);

		// then
		verify(postRepository, times(1)).findPostByIdAndDeletedAtIsNull(postId);
		verify(commentRepository, times(1)).save(any(Comment.class));
	}

	@Test
	@DisplayName("댓글 조회 - 성공")
	void getCommentsByPostId_Success() {
		// given
		Long postId = 1L;
		List<Comment> comments = new ArrayList<>();
		comments.add(testComment);
		testPost.setComments(comments);

		when(postRepository.findPostByIdAndDeletedAtIsNull(postId)).thenReturn(testPost);

		// when
		List<Comment> result = communityService.getCommentsByPostId(postId);

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getContent()).isEqualTo("테스트 댓글");
		verify(postRepository, times(1)).findPostByIdAndDeletedAtIsNull(postId);
	}

	@Test
	@DisplayName("대댓글 작성 - 성공")
	void addReply_Success() {
		// given
		Long commentId = 1L;
		CommentDTO replyDTO = new CommentDTO();
		replyDTO.setContent("테스트 대댓글");

		when(commentRepository.findById(commentId)).thenReturn(Optional.of(testComment));
		when(replyRepository.save(any(Reply.class))).thenReturn(testReply);

		// when
		Reply result = communityService.addReply(commentId, replyDTO, userDetails);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).isEqualTo("테스트 대댓글");
		verify(commentRepository, times(1)).findById(commentId);
		verify(replyRepository, times(1)).save(any(Reply.class));
	}

	@Test
	@DisplayName("대댓글 작성 - 실패: 댓글이 존재하지 않음")
	void addReply_CommentNotFound() {
		// given
		Long commentId = 1L;
		CommentDTO replyDTO = new CommentDTO();
		replyDTO.setContent("테스트 대댓글");

		when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(RuntimeException.class, () -> {
			communityService.addReply(commentId, replyDTO, userDetails);
		});
		verify(commentRepository, times(1)).findById(commentId);
		verify(replyRepository, never()).save(any());
	}
}