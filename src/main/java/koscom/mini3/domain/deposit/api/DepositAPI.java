package koscom.mini3.domain.deposit.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import koscom.mini3.domain.deposit.domain.Deposit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "예적금 API", description = "예적금 관련 API")
@RestController
@RequestMapping("/api/deposits")
public class DepositAPI {

    // ✅ Mock 예적금 데이터
    private static final List<Deposit> MOCK_DEPOSITS = Arrays.asList(
            new Deposit("예금A", 3.5, LocalDate.of(2025, 1, 1)),
            new Deposit("예금B", 2.8, LocalDate.of(2024, 12, 1)),
            new Deposit("예금C", 4.2, LocalDate.of(2025, 6, 15))
    );

    // ✅ 이자율 순 정렬 API (내림차순)
    @Operation(
            summary = "이자율 순 정렬된 예적금 목록 조회",
            description = "예적금 목록을 이자율이 높은 순서대로 정렬하여 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 이자율 순으로 정렬된 목록을 반환함")
    @GetMapping("/sort-by-interest")
    public List<Deposit> getDepositsSortedByInterest() {
        return MOCK_DEPOSITS.stream()
                .sorted(Comparator.comparingDouble(Deposit::getInterestRate).reversed())
                .collect(Collectors.toList());
    }

    // ✅ 만기일 순 정렬 API (오름차순)

    @Operation(
            summary = "만기일 순 정렬된 예적금 목록 조회",
            description = "예적금 목록을 만기일이 가까운 순서대로 정렬하여 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 만기일 순으로 정렬된 목록을 반환함")
    @GetMapping("/sort-by-maturity")
    public List<Deposit> getDepositsSortedByMaturity() {
        return MOCK_DEPOSITS.stream()
                .sorted(Comparator.comparing(Deposit::getMaturityDate))
                .collect(Collectors.toList());
    }
}
