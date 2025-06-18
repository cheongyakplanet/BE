package org.cheonyakplanet.be.presentation.controller;

import java.util.List;

import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.application.service.CommunityService;
import org.cheonyakplanet.be.application.service.SubscriptionService;
import org.cheonyakplanet.be.domain.entity.comunity.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/main")
public class HomeController {

	private final SubscriptionService subscriptionService;
	private final CommunityService communityService;

	/**
	 * 가장 인기 있는 지역
	 * @return
	 */
	@GetMapping("/popular-locations")
	@Operation(summary = "가장 인기 있는 지역", description = "회원들이 가장 많이 관심 설정한 지역",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					    "status": "success",
					    "data": [
					        "서울특별시 동대문구",
					        "서울 강동구"
					    ]
					}
					"""))
			)
		}
	)
	public ResponseEntity<?> getPopularLocations() {
		Object result = subscriptionService.getPopularLocationList();
		return ResponseEntity.ok(new ApiResponse<>("success", result));
	}

	/**
	 * 내 관심 지역
	 * @param request
	 * @return
	 */
	@GetMapping("/my-locations")
	@Operation(summary = "내 관심 지역", description = "로그인 후 설정한 관심지역",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  	   "status": "sucess",
					  	   "data": [
					  	     "서울특별시 동대문구",
					  	     "서울특별시 서대문구",
					  	     "서울특별시 서초구",
					  	     "서울특별시 강북구",
					  	     "서울시 용산구"
					  	   ]
					  	 }
					"""))
			)
		})
	public ResponseEntity<?> getMyLocations(HttpServletRequest request) {
		List<String> interestLocals = subscriptionService.getInterestLocalsByEmail(request);
		return ResponseEntity.ok(new ApiResponse<>("success", interestLocals));
	}

	/**
	 * 인기 있는 게시글
	 * @return
	 */
	@GetMapping("/popular-content")
	@Operation(summary = "인기있는 게시글", description = "좋아요 많은 게시글",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					   "status": "success",
					   "data": [
					     {
					       "createdAt": "2025-02-15T11:49:13.190185",
					       "createdBy": null,
					       "updatedAt": "2025-03-26T08:59:46.649097",
					       "updatedBy": "test@test",
					       "deletedAt": null,
					       "deletedBy": null,
					       "id": 6,
					       "username": "tester",
					       "title": "예제 제목",
					       "content": "예제 내용",
					       "views": 506,
					       "likes": 80,
					       "comments": [
					         {
					           "createdAt": "2025-03-13T01:12:00.308858",
					           "createdBy": "test@test",
					           "updatedAt": "2025-03-13T01:12:00.308858",
					           "updatedBy": "test@test",
					           "deletedAt": null,
					           "deletedBy": null,
					           "id": 7,
					           "content": "댓글 내용",
					           "replies": [
					             {
					               "createdAt": "2025-03-13T01:12:20.88548",
					               "createdBy": "test@test",
					               "updatedAt": "2025-03-13T01:12:20.88548",
					               "updatedBy": "test@test",
					               "deletedAt": null,
					               "deletedBy": null,
					               "id": 4,
					               "content": "댓글 내용"
					             }
					           ]
					         }
					       ]
					     }
					   ]
					 }
					"""))
			)
		})
	public ResponseEntity<?> getPopularPosts() {
		List<Post> posts = communityService.getPopularPosts();
		return ResponseEntity.ok(new ApiResponse<>("success", posts));
	}
}
