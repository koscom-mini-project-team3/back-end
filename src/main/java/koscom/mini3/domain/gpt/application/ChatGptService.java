package koscom.mini3.domain.gpt.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import koscom.mini3.domain.deposit.dao.DepositRepository;
import koscom.mini3.domain.gpt.config.ChatGptConfig;
import koscom.mini3.domain.gpt.dto.ChatGptMessage;
import koscom.mini3.domain.gpt.dto.ChatGptRequest;
import koscom.mini3.domain.gpt.dto.QuestionRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
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
    private final DepositRepository depositRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    public Flux<String> ask(QuestionRequest questionRequest) throws JsonProcessingException {
        WebClient client = WebClient.builder()
                .baseUrl(ChatGptConfig.CHAT_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(ChatGptConfig.AUTHORIZATION, ChatGptConfig.BEARER + chatGptConfig.getApiKey())
                .build();

        StringBuilder productsPrompt = new StringBuilder();

        productsPrompt.append("너는 지금부터 내 예금 투자 도우미야\n")
                .append("예금상품정보 줄테니까 비교해줘")
                .append("아래는 예금 상품 정보야:\n");

//        productsPrompt.append("너는 지금부터 내 예금 투자 도우미야\n")
//            .append("예금상품정보 줄테니까 비교해줘")
//            .append("내가 예금상품정보에 대한 pdf를 줄테니 해당 상품에 대해 가이드를 제시해줘\n")
//            .append("이때 사용자에 대한 아래 정보가 없으면 다시 질문을 해서 물어본 뒤 해당 내용을 기반으로 예금상품에 대한 가이드를 제시해줘\n")
//            .append("1. 우대 금리 관련 해당 되는지?\n")
//            .append("2. 얼마 정도 금액을 투자할 수 있는지?\n")
//            .append("3. 가입 기간은 어느 정도로 생각하고 있는지?\n\n")
//            .append("아래는 예금 상품 정보야:\n");

        // ✅ depositRepository에서 pdf_string 조회 후 안전하게 처리

        List<String> depositInfoList = depositRepository.findAllById(questionRequest.getIds())
                .stream()
                .map(deposit -> {
                    String productName = deposit.getProductName() != null ? deposit.getProductName() : "알 수 없는 상품";
                    String pdfString = deposit.getPdfString();

                    if (pdfString == null || pdfString.trim().isEmpty()) {
                        pdfString = "설명 없음";
                    } else {
                        pdfString = pdfString
                                .replaceAll("[\\x00-\\x1F]", "") // ✅ 제어 문자 제거
                                .replace("\"", "\\\""); // ✅ JSON 파싱 오류 방지
                    }

                    return "상품명: " + productName + "\n" +
                            "설명:\n" + pdfString + "\n\n";
                })
                .collect(Collectors.toList());

        if (!depositInfoList.isEmpty()) {
            depositInfoList.forEach(productsPrompt::append);
        } else {
            productsPrompt.append("해당하는 예금 상품 정보를 찾을 수 없습니다.\n");
        }

        //사용자 질문 추가
        productsPrompt.append(questionRequest.getQuestion());

        List<ChatGptMessage> messages = List.of(
                ChatGptMessage.builder()
                        .role(ChatGptConfig.ROLE)
                        .content(productsPrompt.toString())
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
                .bodyToFlux(String.class)
                .flatMap(response -> {
                    try {
                        // 응답을 파싱하여 'delta.content'만 추출
                        JsonNode jsonResponse = objectMapper.readTree(response);
                        JsonNode deltaContent = jsonResponse.path("choices").get(0).path("delta").path("content");

                        if (!deltaContent.isMissingNode()) {
                            // content를 바로 반환하는 Flux로 처리
                            return Flux.just(deltaContent.asText());
                        } else {
                            // content가 없는 경우 빈 Flux 반환
                            return Flux.empty();
                        }
                    } catch (JsonProcessingException e) {
                        return Flux.empty();
                    }
                });
    }

    public Mono<String> askSingleResponse(QuestionRequest questionRequest) throws JsonProcessingException {

        WebClient client = WebClient.builder()
            .baseUrl(ChatGptConfig.CHAT_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(ChatGptConfig.AUTHORIZATION, ChatGptConfig.BEARER + chatGptConfig.getApiKey())
            .build();

//        String prompt = PromptBuilder.buildPrompt(questionRequest.getQuestion());
        StringBuilder productsPrompt = new StringBuilder();

        productsPrompt.append("너는 지금부터 내 예금 투자 도우미야\n")
            .append("예금상품정보 줄테니까 비교해줘")
            .append("아래는 예금 상품 정보야:\n");

//        productsPrompt.append("너는 지금부터 내 예금 투자 도우미야\n")
//            .append("예금상품정보 줄테니까 비교해줘")
//            .append("내가 예금상품정보에 대한 pdf를 줄테니 해당 상품에 대해 가이드를 제시해줘\n")
//            .append("이때 사용자에 대한 아래 정보가 없으면 다시 질문을 해서 물어본 뒤 해당 내용을 기반으로 예금상품에 대한 가이드를 제시해줘\n")
//            .append("1. 우대 금리 관련 해당 되는지?\n")
//            .append("2. 얼마 정도 금액을 투자할 수 있는지?\n")
//            .append("3. 가입 기간은 어느 정도로 생각하고 있는지?\n\n")
//            .append("아래는 예금 상품 정보야:\n");

        // ✅ depositRepository에서 pdf_string 조회 후 안전하게 처리

        List<String> depositInfoList = depositRepository.findAllById(questionRequest.getIds())
            .stream()
            .map(deposit -> {
                String productName = deposit.getProductName() != null ? deposit.getProductName() : "알 수 없는 상품";
                String pdfString = deposit.getPdfString();

                if (pdfString == null || pdfString.trim().isEmpty()) {
                    pdfString = "설명 없음";
                } else {
                    pdfString = pdfString
                        .replaceAll("[\\x00-\\x1F]", "") // ✅ 제어 문자 제거
                        .replace("\"", "\\\""); // ✅ JSON 파싱 오류 방지
                }

                return "상품명: " + productName + "\n" +
                    "설명:\n" + pdfString + "\n\n";
            })
            .collect(Collectors.toList());

        if (!depositInfoList.isEmpty()) {
            depositInfoList.forEach(productsPrompt::append);
        } else {
            productsPrompt.append("해당하는 예금 상품 정보를 찾을 수 없습니다.\n");
        }

        //사용자 질문 추가
        productsPrompt.append(questionRequest.getQuestion());

        List<ChatGptMessage> messages = new ArrayList<>(Arrays.asList(
            ChatGptMessage.builder()
                .role("system")
                .content(productsPrompt.toString())
                .build(),
            ChatGptMessage.builder()
                .role("user")
                .content(productsPrompt.toString())
                .build()
        ));

        ChatGptRequest chatGptRequest = ChatGptRequest.builder()
            .model(ChatGptConfig.CHAT_MODEL)
            .maxTokens(ChatGptConfig.MAX_TOKEN)
            .temperature(ChatGptConfig.TEMPERATURE)
            .stream(false)
            .messages(messages)
            .build();

        String requestValue = objectMapper.writeValueAsString(chatGptRequest);

        return client.post()
            .bodyValue(requestValue)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class);
    }

    public Flux<String> askTerm(String term) throws JsonProcessingException {
        WebClient client = WebClient.builder()
                .baseUrl(ChatGptConfig.CHAT_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(ChatGptConfig.AUTHORIZATION, ChatGptConfig.BEARER + chatGptConfig.getApiKey())
                .build();

        StringBuilder productsPrompt = new StringBuilder();

        productsPrompt.append("아래 용어에 대해 한줄로 요약해서 설명해줘\n");
        productsPrompt.append(term);

        List<ChatGptMessage> messages = List.of(
                ChatGptMessage.builder()
                        .role(ChatGptConfig.ROLE)
                        .content(productsPrompt.toString())
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
                .bodyToFlux(String.class)
                .flatMap(response -> {
                    try {
                        // 응답을 파싱하여 'delta.content'만 추출
                        JsonNode jsonResponse = objectMapper.readTree(response);
                        JsonNode deltaContent = jsonResponse.path("choices").get(0).path("delta").path("content");

                        if (!deltaContent.isMissingNode()) {
                            // content를 바로 반환하는 Flux로 처리
                            return Flux.just(deltaContent.asText());
                        } else {
                            // content가 없는 경우 빈 Flux 반환
                            return Flux.empty();
                        }
                    } catch (JsonProcessingException e) {
                        return Flux.error(new RuntimeException("Error parsing response", e));
                    }
                });
    }
}
