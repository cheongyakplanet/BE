package org.cheonyakplanet.be.infrastructure.scheduler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.cheonyakplanet.be.application.service.InfoService;
import org.cheonyakplanet.be.application.service.NewsService;
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
	private final NewsService newsService;

	@Scheduled(cron = "0 0 1 ? * *", zone = "Asia/Seoul")
	public void weeklySubscriptionAPTUpdate() {
		log.info("Weekly APT subscription update 시작");
		subscriptionService.updateSubAPT();
		log.info("Weekly APT subscription update 완료");
	}

	@Scheduled(cron = "0 15 1 ? * *", zone = "Asia/Seoul")
	public void weeklySubscriptionCoordinatesUpdate() {
		log.info("Weekly Subscription Coordinates update 시작");
		subscriptionService.updateAllSubscriptionCoordinates();
		log.info("Weekly Subscription Coordinates update 완료");
	}

	@Scheduled(cron = "0 30 1 ? * *", zone = "Asia/Seoul")
	public void runPythonSupplyScript() {
		log.info("Python 스크립트 실행 시작");
		try {
			Path path = Paths.get("scripts", "additional_info.py").toAbsolutePath();
			log.info("📂 Python 스크립트 경로: {}", path);  // 로그로 경로 출력
			ProcessBuilder pb = new ProcessBuilder("python", path.toString());
			pb.redirectErrorStream(true);
			Process p = pb.start();

			// 결과 로그 출력
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					log.info("[Python]: " + line);
				}
			}
			int exitCode = p.waitFor();
			log.info("Python 스크립트 종료 (exitCode={})", exitCode);
		} catch (Exception e) {
			log.error("Python 스크립트 실행 중 오류 발생", e);
		}
	}

	@Scheduled(cron = "0 0 2 L * ?", zone = "Asia/Seoul")
	public void monthlyRealPriceUpdate() {
		String callDate = java.time.format.DateTimeFormatter.ofPattern("yyyyMM").
			format(java.time.LocalDate.now().minusMonths(1));
		log.info("RealEstate batch start for {}", callDate);
		infoService.collectRealPrice(callDate);
		log.info("APT 실거래가 갱신 완료");
	}

	@Scheduled(cron = "0 30 0 * * ?", zone = "Asia/Seoul")
	public void dailyNewsUpdate() {
		log.info("일일 부동산 뉴스 요약 생성 시작");
		try {
			newsService.crawlAndCreateNewsPosts();
			log.info("일일 부동산 뉴스 요약 생성 완료");
		} catch (Exception e) {
			log.error("부동산 뉴스 요약 생성 중 오류 발생", e);
		}
	}
}
