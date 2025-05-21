package org.cheonyakplanet.be.infrastructure.scheduler;

import org.cheonyakplanet.be.application.service.InfoService;
import org.cheonyakplanet.be.application.service.SubscriptionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {
	
	private final InfoService infoService;
	private final SubscriptionService subscriptionService;

	@Scheduled(cron = "0 0 3 ? * MON", zone = "Asia/Seoul")
	public void weeklySubscriptionAPTUpdate() {
		log.info("Weekly APT subscription update 시작");
		subscriptionService.updateSubAPT();
		log.info("Weekly APT subscription update 완료");
	}

	@Scheduled(cron = "0 0 2 L * ?", zone = "Asia/Seoul")
	public void dailyJob() {
		String callDate = java.time.format.DateTimeFormatter.ofPattern("yyyyMM").
			format(java.time.LocalDate.now().minusMonths(1));
		log.info("RealEstate batch start for {}", callDate);
		infoService.collectRealPrice(callDate);
		log.info("APT 실거래가 갱신 완료");
	}
}
