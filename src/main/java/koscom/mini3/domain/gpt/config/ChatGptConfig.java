package koscom.mini3.domain.gpt.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class ChatGptConfig {
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String CHAT_MODEL = "gpt-4o";
    public static final Integer MAX_TOKEN = 15000;
    public static final Boolean STREAM = true;
    public static final String ROLE = "user";
    public static final Double TEMPERATURE = 0.1;
    public static final String CHAT_URL = "https://api.openai.com/v1/chat/completions";

    @Value("${openai.api.key}")
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}

