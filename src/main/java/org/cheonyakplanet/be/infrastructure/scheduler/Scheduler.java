package org.cheonyakplanet.be.infrastructure.scheduler;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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

	@Scheduled(cron = "0 30 3 ? * MON", zone = "Asia/Seoul") // 매주 월요일 03:30
	public void runPythonSupplyScript() {
		log.info("Python 스크립트 실행 시작");
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("python3", "/home/ubuntu/BE/scripts/additional_info.py");
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();

			// 결과 로그 출력
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					log.info("[Python]: " + line);
				}
			}
			int exitCode = process.waitFor();
			log.info("Python 스크립트 종료 (exitCode={})", exitCode);
		} catch (Exception e) {
			log.error("Python 스크립트 실행 중 오류 발생", e);
		}
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
