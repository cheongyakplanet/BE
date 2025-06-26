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
import org.cheonyakplanet.be.application.dto.community.PostUpdateRequestDTO;
import org.cheonyakplanet.be.domain.entity.community.Comment;
import org.cheonyakplanet.be.domain.entity.community.Post;
import org.cheonyakplanet.be.domain.entity.community.PostReaction;
import org.cheonyakplanet.be.domain.entity.community.PostCategory;
import org.cheonyakplanet.be.domain.entity.community.ReactionType;
import org.cheonyakplanet.be.domain.entity.community.Reply;
import org.cheonyakplanet.be.domain.entity.user.User;
import org.cheonyakplanet.be.domain.entity.user.UserRoleEnum;
import org.cheonyakplanet.be.domain.exception.CustomException;
import org.cheonyakplanet.be.domain.exception.ErrorCode;
import org.cheonyakplanet.be.domain.repository.CommentRepository;
import org.cheonyakplanet.be.domain.repository.PostReactionRepository;
import org.cheonyakplanet.be.domain.repository.PostRepository;
import org.cheonyakplanet.be.domain.repository.ReplyRepository;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
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
		postCreateDTO.setPostCategory("CHITCHAT");

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
		// Soft delete only sets deletedBy field, no repository.delete() call
		assertThat(testPost.getDeletedBy()).isEqualTo("tester");
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

	@Test
	@DisplayName("게시글 상세 조회 - 성공 (로그인된 사용자)")
	void getPostById_Success_WithUser() {
		// given
		Long postId = 1L;
		when(postRepository.findPostByIdAndDeletedAtIsNull(postId)).thenReturn(testPost);
		when(reactionRepository.findByPostAndEmail(testPost, userDetails.getUsername()))
			.thenReturn(Optional.empty());

		// when
		var result = communityService.getPostById(postId, userDetails);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTitle()).isEqualTo("테스트 제목");
		verify(postRepository, times(1)).findPostByIdAndDeletedAtIsNull(postId);
		verify(reactionRepository, times(1)).findByPostAndEmail(testPost, userDetails.getUsername());
	}

	@Test
	@DisplayName("게시글 상세 조회 - 성공 (비로그인 사용자)")
	void getPostById_Success_WithoutUser() {
		// given
		Long postId = 1L;
		when(postRepository.findPostByIdAndDeletedAtIsNull(postId)).thenReturn(testPost);

		// when
		var result = communityService.getPostById(postId, null);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTitle()).isEqualTo("테스트 제목");
		verify(postRepository, times(1)).findPostByIdAndDeletedAtIsNull(postId);
		verify(reactionRepository, never()).findByPostAndEmail(any(), any());
	}

	@Test
	@DisplayName("게시글 상세 조회 - 실패: 게시글이 존재하지 않음")
	void getPostById_NotFound() {
		// given
		Long postId = 1L;
		when(postRepository.findPostByIdAndDeletedAtIsNull(postId)).thenThrow(new RuntimeException("Post not found"));

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			communityService.getPostById(postId, userDetails);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMU001);
		verify(postRepository, times(1)).findPostByIdAndDeletedAtIsNull(postId);
	}

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

	@Test
	@DisplayName("게시글 목록 조회 - 좋아요순 정렬")
	void getAllPosts_SortByLikes() {
		// given
		String sort = "likes";
		int page = 0;
		int size = 10;
		Pageable pageable = PageRequest.of(page, size, Sort.by("likes").descending());

		List<Post> posts = List.of(testPost);
		Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

		when(postRepository.findAllByDeletedAtIsNullAndIsBlindIsFalse(pageable)).thenReturn(postPage);

		// when
		Page<PostDTO> result = communityService.getAllPosts(sort, page, size);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		verify(postRepository, times(1)).findAllByDeletedAtIsNullAndIsBlindIsFalse(pageable);
	}

	@Test
	@DisplayName("게시글 목록 조회 - 조회수순 정렬")
	void getAllPosts_SortByViews() {
		// given
		String sort = "views";
		int page = 0;
		int size = 10;
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "views"));

		List<Post> posts = List.of(testPost);
		Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

		when(postRepository.findAllByDeletedAtIsNullAndIsBlindIsFalse(pageable)).thenReturn(postPage);

		// when
		Page<PostDTO> result = communityService.getAllPosts(sort, page, size);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		verify(postRepository, times(1)).findAllByDeletedAtIsNullAndIsBlindIsFalse(pageable);
	}

	@Test
	@DisplayName("게시글 목록 조회 - 기본 정렬 (시간순)")
	void getAllPosts_DefaultSort() {
		// given
		String sort = "unknown";
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
		verify(postRepository, times(1)).findAllByDeletedAtIsNullAndIsBlindIsFalse(pageable);
	}

	@Test
	@DisplayName("게시글 싫어요 - 성공")
	void dislikePost_Success() {
		// given
		Long postId = 1L;
		when(postRepository.findPostByIdAndDeletedAtIsNull(postId)).thenReturn(testPost);
		when(reactionRepository.findByPostAndEmail(testPost, userDetails.getUsername()))
			.thenReturn(Optional.empty());

		// when
		communityService.dislikePost(postId, userDetails);

		// then
		verify(postRepository, times(1)).findPostByIdAndDeletedAtIsNull(postId);
		verify(reactionRepository, times(1)).findByPostAndEmail(testPost, userDetails.getUsername());
		verify(reactionRepository, times(1)).save(any(PostReaction.class));
		verify(postRepository, times(1)).save(testPost);
	}

	@Test
	@DisplayName("게시글 싫어요 - 실패: 로그인하지 않음")
	void dislikePost_NotLoggedIn() {
		// given
		Long postId = 1L;

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			communityService.dislikePost(postId, null);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SIGN000);
		verify(postRepository, never()).findPostByIdAndDeletedAtIsNull(any());
	}

	@Test
	@DisplayName("게시글 싫어요 - 실패: 게시글이 존재하지 않음")
	void dislikePost_PostNotFound() {
		// given
		Long postId = 1L;
		when(postRepository.findPostByIdAndDeletedAtIsNull(postId)).thenReturn(null);

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			communityService.dislikePost(postId, userDetails);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMU001);
		verify(postRepository, times(1)).findPostByIdAndDeletedAtIsNull(postId);
	}

	@Test
	@DisplayName("게시글 좋아요 - 실패: 로그인하지 않음")
	void likePost_NotLoggedIn() {
		// given
		Long postId = 1L;

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			communityService.likePost(postId, null);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SIGN000);
		verify(postRepository, never()).findPostByIdAndDeletedAtIsNull(any());
	}

	@Test
	@DisplayName("게시글 좋아요 - 실패: 게시글이 존재하지 않음")
	void likePost_PostNotFound() {
		// given
		Long postId = 1L;
		when(postRepository.findPostByIdAndDeletedAtIsNull(postId)).thenReturn(null);

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			communityService.likePost(postId, userDetails);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMU001);
		verify(postRepository, times(1)).findPostByIdAndDeletedAtIsNull(postId);
	}

	@Test
	@DisplayName("댓글 ID로 댓글 조회 - 성공")
	void getCommentById_Success() {
		// given
		Long commentId = 1L;
		when(commentRepository.findById(commentId)).thenReturn(Optional.of(testComment));

		// when
		Comment result = communityService.getCommentById(commentId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(commentId);
		verify(commentRepository, times(1)).findById(commentId);
	}

	@Test
	@DisplayName("댓글 ID로 댓글 조회 - 실패: 댓글이 존재하지 않음")
	void getCommentById_NotFound() {
		// given
		Long commentId = 1L;
		when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

		// when & then
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			communityService.getCommentById(commentId);
		});

		assertThat(exception.getMessage()).isEqualTo("Comment not found");
		verify(commentRepository, times(1)).findById(commentId);
	}

	@Test
	@DisplayName("게시글 수정 - 성공")
	void updatePost_Success() {
		// given
		Long postId = 1L;
		PostUpdateRequestDTO updateRequest = new PostUpdateRequestDTO(
			"수정된 제목", "수정된 내용", PostCategory.INFO_SHARE
		);

		when(postRepository.findByIdAndDeletedAtIsNullAndBlindIsFalse(postId))
			.thenReturn(Optional.of(testPost));

		// when
		PostDTO result = communityService.updatePost(postId, updateRequest, userDetails);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTitle()).isEqualTo("수정된 제목");
		assertThat(result.getContent()).isEqualTo("수정된 내용");
		verify(postRepository, times(1)).findByIdAndDeletedAtIsNullAndBlindIsFalse(postId);
	}

	@Test
	@DisplayName("게시글 수정 - 실패: 게시글이 존재하지 않음")
	void updatePost_PostNotFound() {
		// given
		Long postId = 1L;
		PostUpdateRequestDTO updateRequest = new PostUpdateRequestDTO(
			"수정된 제목", "수정된 내용", PostCategory.INFO_SHARE
		);

		when(postRepository.findByIdAndDeletedAtIsNullAndBlindIsFalse(postId))
			.thenReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			communityService.updatePost(postId, updateRequest, userDetails);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMU001);
		verify(postRepository, times(1)).findByIdAndDeletedAtIsNullAndBlindIsFalse(postId);
	}

	@Test
	@DisplayName("게시글 수정 - 실패: 권한 없음")
	void updatePost_UnauthorizedUser() {
		// given
		Long postId = 1L;
		PostUpdateRequestDTO updateRequest = new PostUpdateRequestDTO(
			"수정된 제목", "수정된 내용", PostCategory.INFO_SHARE
		);

		Post otherUserPost = Post.builder()
			.id(postId)
			.username("otherUser")
			.title("다른 사용자의 게시글")
			.content("내용")
			.build();

		when(postRepository.findByIdAndDeletedAtIsNullAndBlindIsFalse(postId))
			.thenReturn(Optional.of(otherUserPost));

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			communityService.updatePost(postId, updateRequest, userDetails);
		});

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.COMU002);
		verify(postRepository, times(1)).findByIdAndDeletedAtIsNullAndBlindIsFalse(postId);
	}

	@Test
	@DisplayName("게시글 수정 - 부분 수정 (제목만)")
	void updatePost_PartialUpdate_TitleOnly() {
		// given
		Long postId = 1L;
		PostUpdateRequestDTO updateRequest = new PostUpdateRequestDTO(
			"수정된 제목", null, null
		);

		when(postRepository.findByIdAndDeletedAtIsNullAndBlindIsFalse(postId))
			.thenReturn(Optional.of(testPost));

		// when
		PostDTO result = communityService.updatePost(postId, updateRequest, userDetails);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTitle()).isEqualTo("수정된 제목");
		assertThat(result.getContent()).isEqualTo("테스트 내용"); // 기존 내용 유지
		verify(postRepository, times(1)).findByIdAndDeletedAtIsNullAndBlindIsFalse(postId);
	}
}