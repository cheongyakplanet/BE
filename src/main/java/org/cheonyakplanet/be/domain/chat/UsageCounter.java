package org.cheonyakplanet.be.domain.chat;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class UsageCounter {
	private int count;
	private LocalDate lastReset = LocalDate.now();

	public boolean canUse() {
		if (!LocalDate.now().equals(lastReset)) {
			count = 0;
			lastReset = LocalDate.now();
		}
		return count < 15;
	}

	public void increment() {
		count++;
	}

	public void reset() {
		count = 0;
		lastReset = LocalDate.ofEpochDay(System.currentTimeMillis());
	}
}
