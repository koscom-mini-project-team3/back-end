package koscom.mini3.domain.gpt.api;

import java.util.List;
import koscom.mini3.domain.gpt.dto.QuestionRequest;
import koscom.mini3.domain.gpt.application.ChatGptService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/gpt")
@RequiredArgsConstructor
public class ChatGptController {
    private final ChatGptService chatGptService;

    @PostMapping(value = "ask-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> ask(@RequestBody QuestionRequest questionRequest) {
        try {
            return chatGptService.ask(questionRequest);
        } catch (JsonProcessingException e) {
            return Flux.empty();
        }
    }

    @PostMapping(value = "ask-single", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> askSingle(@RequestBody QuestionRequest questionRequest) {
        try {
            return chatGptService.askSingleResponse(questionRequest);
        } catch (JsonProcessingException e) {
            return Mono.just("{\"error\": \"JSON processing error\"}");
        }
    }

}

