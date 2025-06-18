package org.cheonyakplanet.be.infrastructure;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.cheonyakplanet.be.domain.chat.ChatMessage;
import org.cheonyakplanet.be.domain.chat.ChatSession;
import org.cheonyakplanet.be.domain.chat.UsageCounter;
import org.cheonyakplanet.be.infrastructure.client.GeminiClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
	private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
	private final Map<String, ChatSession> chatSessions = new ConcurrentHashMap<>();
	private final Map<String, UsageCounter> usageCounters = new ConcurrentHashMap<>();
	private final GeminiClient geminiClient;

	public ChatWebSocketHandler(GeminiClient geminiClient) {
		this.geminiClient = geminiClient;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String user = (String)session.getAttributes().get("userEmail");
		if (user == null) {
			session.sendMessage(new TextMessage("로그인이 필요합니다."));
			session.close();
			return;
		}
		if (activeSessions.containsKey(user)) {
			session.sendMessage(new TextMessage("이미 연결된 세션이 존재합니다."));
			session.close();
			return;
		}
		activeSessions.put(user, session);
		chatSessions.put(user, new ChatSession());
		usageCounters.putIfAbsent(user, new UsageCounter());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		String user = (String)session.getAttributes().get("userEmail");
		if (user != null) {
			WebSocketSession removedSession = activeSessions.remove(user);
			ChatSession removedChatSession = chatSessions.remove(user);

			if (removedChatSession != null) {
				removedChatSession.clear();
			}
		}

	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String user = (String)session.getAttributes().get("userEmail");
		// log.error("메시지 처리 시작 - 사용자: " + user + ", 메시지: " + message.getPayload());

		if (user == null) {
			// log.error("사용자 정보가 null입니다!");
			return;
		}

		UsageCounter counter = usageCounters.get(user);
		// log.error("현재 사용량 : " + counter.getCount());
		if (!counter.canUse()) {
			// log.error("사용량 초과로 연결 종료");
			session.sendMessage(new TextMessage("하루 사용량(15회)을 초과하셨습니다."));
			session.close();
			return;
		}

		ChatSession chatSession = chatSessions.get(user);
		chatSession.addMessage("user", message.getPayload());

		// log.error("Gemini API 호출 시작...");

		geminiClient.ask(chatSession.getContext()).subscribe(
			response -> {
				try {
					// log.error("Gemini 응답 받음: " + response);
					chatSession.addMessage("model", response);
					session.sendMessage(new TextMessage(response));
					// log.error("클라이언트에게 응답 전송 완료");
					counter.increment();

					if (chatSession.messageCount() >= 5) {
						geminiClient.ask(
								List.of(new ChatMessage("user", "다음 대화를 요약해줘:\n" + chatSession.recentMessagesToText())))
							.subscribe(summary -> {
								chatSession.updateSummary(summary);
								chatSession.clearRecentMessages();
							});
					}
				} catch (IOException e) {
					// log.error("응답 전송 중 오류: " + e.getMessage());
					e.printStackTrace();
				}
			},
			error -> {
				// log.error("Gemini API 오류: " + error.getMessage());
				error.printStackTrace();
			}
		);
	}

	@Scheduled(cron = "0 0 0 * * *") // 매일 00:00:00
	public void resetDailyCounters() {
		System.out.println("일일 사용량 카운터 초기화 시작...");

		int userCount = usageCounters.size();
		usageCounters.values().forEach(UsageCounter::reset);

		System.out.println("일일 사용량 초기화 완료: " + userCount + "명의 사용자");

		// 메모리 정리 힌트
		System.gc();
	}
}
