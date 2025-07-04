package org.cheonyakplanet.be.presentation.controller;

import java.util.Optional;

import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.application.dto.community.CommentDTO;
import org.cheonyakplanet.be.application.dto.community.PostCreateDTO;
import org.cheonyakplanet.be.application.dto.community.PostDTO;
import org.cheonyakplanet.be.application.dto.community.PostDetailDTO;
import org.cheonyakplanet.be.application.dto.community.PostUpdateRequestDTO;
import org.cheonyakplanet.be.application.service.CommunityService;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/community")
public class CommunityController {
	private final CommunityService communityService;

	@PostMapping("/posts")
	@Operation(summary = "게시글 작성")
	public ResponseEntity<?> createPost(@RequestBody PostCreateDTO postCreateDTO,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		communityService.createPost(postCreateDTO, userDetails);
		return ResponseEntity.ok(new ApiResponse<>("success", "게시글 작성 완료"));
	}

	@GetMapping("/posts")
	@Operation(summary = "모든 게시글 조회 (정렬 기준: time, views, likes; 페이징 기능 포함)")
	public ResponseEntity<?> getAllPosts(
		@RequestParam(value = "sort", defaultValue = "time") String sort,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size
	) {
		Page<PostDTO> result = communityService.getAllPosts(sort, page, size);
		return ResponseEntity.ok(new ApiResponse("success", result));
	}

	@GetMapping("/post/{id}")
	@Operation(summary = "게시글 한 건 조회")
	public ResponseEntity<?> getPost(@PathVariable("id") Long id,
		@AuthenticationPrincipal Optional<UserDetailsImpl> optionalUserDetails) {
		UserDetailsImpl userDetails = (optionalUserDetails != null) ? optionalUserDetails.orElse(null) : null;
		PostDetailDTO result = communityService.getPostById(id, userDetails);
		return ResponseEntity.ok(new ApiResponse("success", result));
	}

	@DeleteMapping("/post/{id}")
	@Operation(summary = "게시글 삭제")
	public ResponseEntity<?> deletePost(@PathVariable("id") Long id,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		communityService.deletePost(id, userDetails);
		return ResponseEntity.ok(new ApiResponse<>("success", "게시글 삭제 완료"));
	}

	@PostMapping("/post/like/{id}")
	@Operation(summary = "게시글 좋아요")
	public ResponseEntity<?> likePost(@PathVariable("id") Long id,
		@AuthenticationPrincipal Optional<UserDetailsImpl> optionalUserDetails) {
		UserDetailsImpl userDetails = (optionalUserDetails != null) ? optionalUserDetails.orElse(null) : null;
		communityService.likePost(id, userDetails);
		return ResponseEntity.ok(new ApiResponse<>("success", "좋아요 +1"));
	}

	@GetMapping("/post/my")
	@Operation(summary = "내가 쓴 글 확인")
	public ResponseEntity<?> getMyPosts(@RequestParam(value = "sort", defaultValue = "time") String sort,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		Page<PostDTO> result = communityService.getMyPosts(sort, page, size, userDetails);
		return ResponseEntity.ok(new ApiResponse<>("success", result));
	}

	@PostMapping("/post/dislike/{id}")
	@Operation(summary = "게시글 싫어요")
	public ResponseEntity<?> dislikePost(@PathVariable("id") Long id,
		@AuthenticationPrincipal Optional<UserDetailsImpl> optionalUserDetails) {
		UserDetailsImpl userDetails = (optionalUserDetails != null) ? optionalUserDetails.orElse(null) : null;
		communityService.dislikePost(id, userDetails);
		return ResponseEntity.ok(new ApiResponse<>("success", "싫어요 +1"));
	}

	@PostMapping("/comment/{postId}")
	@Operation(summary = "게시글에 댓글 작성")
	public ResponseEntity<?> addComment(@PathVariable("postId") Long postId, @RequestBody CommentDTO commentDTO,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		communityService.addComment(postId, commentDTO, userDetails);
		return ResponseEntity.ok(new ApiResponse<>("success", "댓글 작성 완료"));
	}

	@PostMapping("/comment/comment/{commentId}")
	@Operation(summary = "게시글의 댓글에 답글 작성")
	public ResponseEntity<?> addReply(@PathVariable("commentId") Long commentId, @RequestBody CommentDTO commentDTO,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		communityService.addReply(commentId, commentDTO, userDetails);
		return ResponseEntity.ok(new ApiResponse<>("success", "대댓글 작성 완료"));
	}

	@PatchMapping("/post/{id}")
	@Operation(summary = "게시글 수정", description = "작성자가 본인 글을 수정합니다.")
	public ResponseEntity<ApiResponse<PostDTO>> updatePost(
		@PathVariable("id") Long postId,
		@RequestBody PostUpdateRequestDTO request,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {

		PostDTO updated = communityService.updatePost(postId, request, userDetails);
		return ResponseEntity.ok(new ApiResponse<>("success", updated));
	}
}
