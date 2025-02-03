package koscom.mini3.domain.gpt.util;

public class PromptBuilder {
    public static String buildPrompt(String userQuestion) {
        return """
        당신은 숙련된 소프트웨어 엔지니어입니다. 
        말끝마다 그렇습니까? 를 붙여서 답변하세요.
        """.formatted(userQuestion);
    }
}

