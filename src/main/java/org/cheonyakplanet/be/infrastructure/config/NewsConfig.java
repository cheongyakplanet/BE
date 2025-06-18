package org.cheonyakplanet.be.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "news")
public class NewsConfig {

	private Api api = new Api();
	private Scheduling scheduling = new Scheduling();

	@Data
	public static class Api {
		private String key;
		private int maxResults = 10;
		private int delayBetweenRequests = 1000; // milliseconds
	}

	@Data
	public static class Scheduling {
		private boolean enabled = true;
		private String cron = "0 0 9 * * ?"; // 매일 오전 9시
		private int maxDailyPosts = 20;
	}
}