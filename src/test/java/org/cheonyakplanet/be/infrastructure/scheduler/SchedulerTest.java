package org.cheonyakplanet.be.infrastructure.scheduler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.cheonyakplanet.be.application.service.InfoService;
import org.cheonyakplanet.be.application.service.SubscriptionService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class SchedulerTest {
	@Autowired
	private Scheduler scheduler;

	@MockBean
	private SubscriptionService subscriptionService;

	@MockBean
	private InfoService infoService;

	@Test
	void weeklySubscriptionAPTUpdate_shouldInvokeUpdateSubAPT() {
		// when
		scheduler.weeklySubscriptionAPTUpdate();

		// then
		verify(subscriptionService, times(1)).updateSubAPT();
	}

	@Test
	void monthlyRealPriceUpdate_shouldInvokeCollectRealPriceWithPreviousMonth() {
		// when
		scheduler.monthlyRealPriceUpdate();

		// then
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(infoService, times(1)).collectRealPrice(captor.capture());

		String actualCallDate = captor.getValue();
		String expectedCallDate = YearMonth.now()
			.minusMonths(1)
			.format(DateTimeFormatter.ofPattern("yyyyMM"));
		assertEquals(expectedCallDate, actualCallDate,
			"collectRealPrice() 에 전달된 callDate 가 전월(yyyyMM) 이어야 합니다.");
	}

}