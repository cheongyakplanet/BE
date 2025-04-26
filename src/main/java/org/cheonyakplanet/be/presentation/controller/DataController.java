package org.cheonyakplanet.be.presentation.controller;

import java.util.List;

import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.application.dto.subscriprtion.CoordinateResponseDTO;
import org.cheonyakplanet.be.application.service.FinanceService;
import org.cheonyakplanet.be.application.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/data")
public class DataController {
	private final SubscriptionService subscriptionService;
	private final FinanceService financeService;

	@GetMapping("/subscription/apartment")
	@Operation(summary = "아파트 청약 불러오기", description = "swagger")
	public ResponseEntity<?> getSubscriptionData() {
		String result = subscriptionService.updateSubAPT();
		return ResponseEntity.ok(new ApiResponse<>("success", result));
	}

	@PutMapping("/updateAllCoordinates")
	@Operation(summary = "모든 SubscriptionInfo 행의 위도/경도 업데이트", description = "SubscriptionLocationInfo에 저장")
	public ResponseEntity<?> updateAllCoordinates() {
		List<CoordinateResponseDTO> coordinateResponses = subscriptionService.updateAllSubscriptionCoordinates();
		return ResponseEntity.ok(new ApiResponse<>("success", coordinateResponses));
	}

	@GetMapping("/mortgage")
	@Operation(summary = "모기지 상품 불러오기")
	public ResponseEntity<?> getFinanceData() {
		String result = financeService.updateMortgageLoan();
		return ResponseEntity.ok(new ApiResponse<>("success", result));
	}

	@GetMapping("/hosueloan")
	@Operation(summary = "주택담보 대출 상품 불어오기")
	public ResponseEntity<?> getHouseLoanData() {
		String result = financeService.updateRenthouse();
		return ResponseEntity.ok(new ApiResponse<>("success", result));
	}
}
