package org.cheonyakplanet.be.domain.service;

import lombok.RequiredArgsConstructor;
import org.cheonyakplanet.be.application.dto.community.CommentDTO;
import org.cheonyakplanet.be.application.dto.community.PostCreateDTO;
import org.cheonyakplanet.be.application.dto.community.PostDTO;
import org.cheonyakplanet.be.domain.entity.Comment;
import org.cheonyakplanet.be.domain.entity.Post;
import org.cheonyakplanet.be.domain.entity.Reply;
import org.cheonyakplanet.be.domain.entity.User;
import org.cheonyakplanet.be.domain.repository.CommentRepository;
import org.cheonyakplanet.be.domain.repository.PostRepository;
import org.cheonyakplanet.be.domain.repository.ReplyRepository;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    public Post createPost(PostCreateDTO postCreateDTO, UserDetailsImpl userDetails) {

        User user = userDetails.getUser();

        Post post = Post.builder()
                .username(user.getUsername())
                .title(postCreateDTO.getTitle())
                .content(postCreateDTO.getContent())
                .likes(0)
                .build();
        return postRepository.save(post);
    }

    public void deletePost(Long postId, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        // 삭제할 게시글 조회 (존재하지 않으면 예외 발생)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMU001, "해당 게시글이 존재하지 않습니다."));

        // 게시글 작성자와 요청 사용자의 username이 일치하는지 확인
        if (!post.getUsername().equals(user.getUsername())) {
            throw new CustomException(ErrorCode.COMU002, "게시글 삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    /**
     * 게시글 전체 조회 기능 (정렬 기준: time, views, likes, 페이징 포함)
     *
     * @param sort 정렬 기준 (time, views, likes)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 당 항목 수
     * @return 페이징 처리된 PostDTO Page 객체
     */
    public Page<PostDTO> getAllPosts(String sort, int page, int size) {
        Sort sortOrder;
        if ("likes".equalsIgnoreCase(sort)) {
            sortOrder = Sort.by("likes").descending();
        } else if ("views".equalsIgnoreCase(sort)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "views");
        } else {
            sortOrder = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<Post> postPage = postRepository.findAllByDeletedAtIsNullAndIsBlindIsFalse(pageable);

        return postPage.map(Post::ToDTO);
    }

    @Transactional
    public Post getPostById(Long id) {
        try {
            Post post = postRepository.findPostById(id);
            post.countViews();
            return post;

        } catch (Exception e) {
            throw new CustomException(ErrorCode.COMU001, e.getMessage());
        }
    }

    public List<Post> getPopularPosts() {
        return postRepository.findPostsOrderByLikes(PageRequest.of(0, 5)); // 상위 5개
    }

    /**
     * 게시글 좋아요
     *
     * @param id
     */
    public void likePost(Long id) {
        Post post = postRepository.findPostById(id);
        post.incrementLikes();
        postRepository.save(post);
    }

    public void dislikePost(Long id) {
        Post post = postRepository.findPostById(id);
        post.decrementLikes();
        postRepository.save(post);
    }

    public void addComment(Long postId, CommentDTO commentDTO, UserDetailsImpl userDetails) {
        Post post = postRepository.findPostById(postId);
        Comment comment = Comment.builder()
                .content(commentDTO.getContent())
                .post(post)
                .build();
        commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        Post post = postRepository.findPostById(postId);
        return post.getComments();
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    public Reply addReply(Long commentId, CommentDTO commentDTO, UserDetailsImpl userDetails) {
        Comment comment = getCommentById(commentId);
        Reply reply = Reply.builder()
                .content(commentDTO.getContent())
                .comment(comment)
                .build();
        return replyRepository.save(reply);
    }
}
