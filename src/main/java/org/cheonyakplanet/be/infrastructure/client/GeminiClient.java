package org.cheonyakplanet.be.infrastructure.client;

import java.util.List;
import java.util.Map;

import org.cheonyakplanet.be.domain.chat.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Component
public class GeminiClient {
	private final WebClient webClient;

	@Value("${gemini.api.key}")
	private String apiKey;

	public GeminiClient(WebClient.Builder builder) {
		this.webClient = builder.baseUrl(
			"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent").build();
	}

	public Mono<String> ask(List<ChatMessage> context) {
		List<Map<String, Object>> contents = context.stream().map(msg -> Map.of(
			"role", msg.role(),
			"parts", List.of(Map.of("text", msg.content()))
		)).toList();

		return webClient.post()
			.uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
			.bodyValue(Map.of("contents", contents))
			.retrieve()
			.bodyToMono(String.class)
			.map(response -> {
				try {
					JsonNode json = new ObjectMapper().readTree(response);
					return json.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
				} catch (Exception e) {
					return "응답 파싱 실패";
				}
			});
	}
}
