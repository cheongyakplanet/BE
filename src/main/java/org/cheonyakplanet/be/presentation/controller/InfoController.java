package org.cheonyakplanet.be.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.application.dto.subscriprtion.SubscriptionDetailDTO;
import org.cheonyakplanet.be.domain.service.InfoService;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/info")
@RequiredArgsConstructor
public class InfoController {

    private final InfoService infoService;

    @GetMapping("/subscription")
    @Operation(summary = "모든 청약 불러오기", description = "간단한 정보만 제공, 마감일 순으로 정렬")
    public ResponseEntity<?> getSubscriptions(@RequestParam(name = "page", defaultValue = "1") int page,
                                              @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(new ApiResponse<>("success", infoService.getSubscriptions(page-1, size)));
    }

    /**
     * 1건의 청약 정보를 불러오기
     *
     * @return
     */
    @GetMapping("/subscription/{id}")
    @Operation(summary = "id로 1건의 청약 물건 조회", description = "요소 클릭시 사용")
    public ResponseEntity<ApiResponse> getSubscription(@PathVariable("id") Long id) {
        Object result = infoService.getSubscriptionById(id);
        return ResponseEntity.ok(new ApiResponse("success", result));
    }

    /**
     * 특정 지역의 청약물건 검색
     *
     * @param region
     * @param city
     * @return
     */
    @GetMapping("/subscription/list")
    @Operation(summary = "지역으로 청약 검색", description = "시, 도 list에서 선택")
    public ResponseEntity<?> getSubscriptionsByRegion(
            @Parameter(description = "지역", example = "서울특별시")
            @RequestParam("region") String region,
            @Parameter(description = "구", example = "서초구")
            @RequestParam("city") String city) {
        Object subscriptions = infoService.getSubscriptionsByRegion(region, city);

        return ResponseEntity.ok(new ApiResponse("success", subscriptions));
    }

    /**
     * 청약 물건의 주변 인프라를 불러오는 메서드
     *
     * @param id
     * @return
     */
    @GetMapping("/subscription/{id}/detail/infra")
    @Operation(summary = "청약 물건의 주변 인프라", description = "미완성")
    public String getSubscriptionDetailInfra(@PathVariable("id") Long id) {
        return null;
    }

    /**
     * 대한민국의 특별시,도 리스트
     *
     * @return
     */
    @GetMapping("/subscription/regionlist")
    @Operation(summary = "대한민국의 특별시,도 리스트", description = "입력을 위한 보기")
    public ResponseEntity<?> getRegionList() {
        List<?> result = infoService.getReginList();
        return ResponseEntity.ok(new ApiResponse("success", result));
    }

    /**
     * 대한민국의 군, 구 리스트
     *
     * @param region
     * @return
     */
    @GetMapping("/subscription/citylist")
    @Operation(summary = "대한민국의 군,구 리스트", description = "입력을 위한 보기")
    public ResponseEntity<?> getCityList(@RequestParam("region") String region) {
        List<?> result = infoService.getCityList(region);
        return ResponseEntity.ok(new ApiResponse("success", result));
    }

    @GetMapping("/subscription/mysubscriptions")
    @Operation(summary = "나의 관심지역 청약 리스트", description = "관심지역이 제대로 등록되어 있어야 함")
    public ResponseEntity<?> getMySubscriptions(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(new ApiResponse<>("success", infoService.getMySubscriptions(userDetails)));
    }
}
