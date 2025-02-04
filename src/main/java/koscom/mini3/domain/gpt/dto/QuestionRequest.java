package koscom.mini3.domain.gpt.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Getter
@NoArgsConstructor
public class QuestionRequest implements Serializable {
    private String question;
    private List<Long> ids;
    private String previousChat;
}
