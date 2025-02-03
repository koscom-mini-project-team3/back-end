package koscom.mini3.domain.gpt.application;

import koscom.mini3.domain.gpt.config.ChatGptConfig;
import koscom.mini3.domain.gpt.dto.ChatGptMessage;
import koscom.mini3.domain.gpt.dto.ChatGptRequest;
import koscom.mini3.domain.gpt.dto.QuestionRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import koscom.mini3.domain.gpt.util.PromptBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatGptService {
    private final ChatGptConfig chatGptConfig;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    public Flux<String> ask(QuestionRequest questionRequest) throws JsonProcessingException {
        WebClient client = WebClient.builder()
                .baseUrl(ChatGptConfig.CHAT_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(ChatGptConfig.AUTHORIZATION, ChatGptConfig.BEARER + chatGptConfig.getApiKey())
                .build();

        List<ChatGptMessage> messages = List.of(
                ChatGptMessage.builder()
                        .role(ChatGptConfig.ROLE)
                        .content(questionRequest.getQuestion())
                        .build()
        );

        ChatGptRequest chatGptRequest = ChatGptRequest.builder()
                .model(ChatGptConfig.CHAT_MODEL)
                .maxTokens(ChatGptConfig.MAX_TOKEN)
                .temperature(ChatGptConfig.TEMPERATURE)
                .stream(ChatGptConfig.STREAM)
                .messages(messages)
                .build();

        String requestValue = objectMapper.writeValueAsString(chatGptRequest);

        return client.post()
                .bodyValue(requestValue)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class);
    }

    public Mono<String> askSingleResponse(QuestionRequest questionRequest) throws JsonProcessingException {
        WebClient client = WebClient.builder()
                .baseUrl(ChatGptConfig.CHAT_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(ChatGptConfig.AUTHORIZATION, ChatGptConfig.BEARER + chatGptConfig.getApiKey())
                .build();

        String prompt = PromptBuilder.buildPrompt(questionRequest.getQuestion());

        List<ChatGptMessage> messages = List.of(
                ChatGptMessage.builder()
                        .role("system")
                        .content("당신은 AI 개발자이며, Spring Boot 및 WebFlux 전문가입니다.")
                        .build(),
                ChatGptMessage.builder()
                        .role("user")
                        .content(prompt)
                        .build()
        );

        ChatGptRequest chatGptRequest = ChatGptRequest.builder()
                .model(ChatGptConfig.CHAT_MODEL)
                .maxTokens(ChatGptConfig.MAX_TOKEN)
                .temperature(ChatGptConfig.TEMPERATURE)
                .stream(false)  // ✅ 스트리밍 비활성화
                .messages(messages)
                .build();

        String requestValue = objectMapper.writeValueAsString(chatGptRequest);

        return client.post()
                .bodyValue(requestValue)
                .accept(MediaType.APPLICATION_JSON)  // ✅ 일반 JSON 응답으로 받음
                .retrieve()
                .bodyToMono(String.class);  // ✅ 전체 응답을 단일 문자열로 변환
    }
}
