package org.cheonyakplanet.be.infrastructure.scheduler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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

	@Scheduled(cron = "0 0 3 ? * MON", zone = "Asia/Seoul")
	public void weeklySubscriptionAPTUpdate() {
		log.info("Weekly APT subscription update 시작");
		subscriptionService.updateSubAPT();
		log.info("Weekly APT subscription update 완료");
	}

	@Scheduled(cron = "0 15 3 ? * MON", zone = "Asia/Seoul")
	public void weeklySubscriptionCoordinatesUpdate() {
		log.info("Weekly Subscription Coordinates update 시작");
		subscriptionService.updateAllSubscriptionCoordinates();
		log.info("Weekly Subscription Coordinates update 완료");
	}

	@Scheduled(cron = "0 30 3 ? * MON", zone = "Asia/Seoul") // 매주 월요일 03:30
	public void runPythonSupplyScript() {
		log.info("Python 스크립트 실행 시작");
		try {
			String userDir = System.getProperty("user.dir");  // 보통 jar 가 실행되는 폴더
			String scriptPath = Paths.get(userDir, "scripts", "additional_info.py")
				.toAbsolutePath()
				.toString();

			ProcessBuilder pb = new ProcessBuilder("python3", scriptPath);
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

	@Scheduled(cron = "0 0 9 * * ?", zone = "Asia/Seoul") // 매일 오전 9시
	public void dailyNewsUpdate() {
		log.info("일일 부동산 뉴스 수집 시작");
		try {
			newsService.crawlAndCreateNewsPosts();
			log.info("일일 부동산 뉴스 수집 완료");
		} catch (Exception e) {
			log.error("부동산 뉴스 수집 중 오류 발생", e);
		}
	}
}
