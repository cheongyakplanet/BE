package org.cheonyakplanet.be.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {
	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
		int cores = Runtime.getRuntime().availableProcessors();
		exec.setCorePoolSize(cores);
		exec.setMaxPoolSize(cores * 2);
		exec.setQueueCapacity(500);
		exec.setThreadNamePrefix("RealEstate-Exec-");
		exec.initialize();
		return exec;
	}
}
