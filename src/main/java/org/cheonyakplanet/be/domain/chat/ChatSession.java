package org.cheonyakplanet.be.domain.chat;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class ChatSession {
	private Deque<ChatMessage> recentMessages = new ArrayDeque<>();
	private String summarizedContext = "";

	public void addMessage(String role, String content) {
		recentMessages.addLast(new ChatMessage(role, content));
		if (recentMessages.size() > 5)
			recentMessages.removeFirst();
	}

	public List<ChatMessage> getContext() {
		List<ChatMessage> result = new ArrayList<>();
		if (!summarizedContext.isBlank()) {
			result.add(new ChatMessage("system", summarizedContext));
		}
		result.addAll(recentMessages);
		return result;
	}

	public void updateSummary(String summary) {
		this.summarizedContext = summary;
	}

	public String recentMessagesToText() {
		return recentMessages.stream().map(ChatMessage::content).collect(Collectors.joining("\n"));
	}

	public int messageCount() {
		return recentMessages.size();
	}

	public void clearRecentMessages() {
		recentMessages.clear();
	}

	public void clear() {
		if (recentMessages != null) {
			recentMessages.clear();
			recentMessages = null;
		}
		summarizedContext = null;
		System.gc(); // 힌트만 제공 (강제는 아님)
	}
}
