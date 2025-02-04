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
            .append("우선 이전에 나눴던 대화 내용들을 줄게\n")
            .append(questionRequest.getPreviousChat())
            .append("여기까지가 이전 채팅 내용이야. 이 내용을 학습하고 이어서 질문 받아줘\n")
            .append("답변에는 자기소개 하지말고 질문에 대한 정보만 출력해줘.\n")
            .append("말투는 공손하게 해줘\n");

        productsPrompt
                .append("출력할때 다른 요소들은 바꾸지 말고 줄바꿈과 글자 강조만 <br>태그와 <b>태그로 HTML 문법에 맞게 출력해줘 이때 글자 강조 전에는 항 줄바꿈을 2번 해줘\n")
                .append("마지막에는 사용자가 필요한 정보를 요청할거야\n")
                .append("예금상품정보를 비교해달라는 요청이 오면 예금상품정보 줄테니까 비교해주고\n")
                .append("예금상품이 나한테 적합한지 알려달라는 요청이 오면 예금 상품 정보 반영해서 다음과 같은 유형의 질문을 해줘\n")
                .append("1. 해당 예금상품 우대 금리에 해당 되사나요??\n")
                .append("1번 질문은 예금 상품 우대금리 조건을 알려주고 해당 되는지 물어봐줘\n")
                .append("2. 얼마 정도 금액을 투자할 수 있으신가요?\n")
                .append("3. 가입 기간은 어느 정도로 생각하고 있으신가요?\n")
                .append("아래는 예금 상품 정보야:\n");



        List<String> depositInfoList = depositRepository.findAllById(questionRequest.getIds())
                .stream()
                .map(deposit -> {
                    String productName = deposit.getProductName() != null ? deposit.getProductName() : "알 수 없는 상품";
                    String pdfString = deposit.getPdfString();

                    if (pdfString == null || pdfString.trim().isEmpty()) {
                        pdfString = "설명 없음";
                    } else {
                        pdfString = pdfString
                                .replaceAll("[\\x00-\\x1F]", "")
                                .replace("\"", "\\\"");
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

        List<String> depositInfoList = depositRepository.findAllById(questionRequest.getIds())
            .stream()
            .map(deposit -> {
                String productName = deposit.getProductName() != null ? deposit.getProductName() : "알 수 없는 상품";
                String pdfString = deposit.getPdfString();

                if (pdfString == null || pdfString.trim().isEmpty()) {
                    pdfString = "설명 없음";
                } else {
                    pdfString = pdfString
                        .replaceAll("[\\x00-\\x1F]", "")
                        .replace("\"", "\\\"");
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
                    return Flux.empty();
                }
            });
    }
}
