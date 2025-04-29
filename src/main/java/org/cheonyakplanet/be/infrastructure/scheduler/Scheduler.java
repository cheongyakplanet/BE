package org.cheonyakplanet.be.infrastructure.scheduler;

import org.cheonyakplanet.be.application.service.InfoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {
	private final InfoService infoService;

	@Scheduled(cron = "0 0 2 L * ?", zone = "Asia/Seoul")
	public void dailyJob() {
		String callDate = java.time.format.DateTimeFormatter.ofPattern("yyyyMM").
			format(java.time.LocalDate.now().minusMonths(1));
		log.info("RealEstate batch start for {}", callDate);
		infoService.ingestAll(callDate);
		infoService.refreshSummary();
		log.info("APT 실거래가 갱신 완료");
	}
}
