package org.cheonyakplanet.be.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cheonyakplanet.be.domain.service.FinanceService;
import org.cheonyakplanet.be.domain.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/data")
public class DataController {
    private final SubscriptionService subscriptionService;
    private final FinanceService financeService;

    @GetMapping("/subapt")
    @Operation(summary = "아파트 청약 불러오기",description = "swagger")
    public ResponseEntity<?> getSubscriptionData(){
        String result = subscriptionService.updateSubAPT();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/mortgage")
    public ResponseEntity<?> getFinanceData(){
        String result = financeService.updateMortgageLoan();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/hosueloan")
    public ResponseEntity<?> getHouseLoanData(){
        String result = financeService.updateRenthouse();
        return ResponseEntity.ok(result);
    }
}
