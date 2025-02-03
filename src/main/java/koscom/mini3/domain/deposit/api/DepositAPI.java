package koscom.mini3.domain.deposit.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import koscom.mini3.domain.deposit.domain.Deposit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
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
            new Deposit(1L, "한국은행", "정기예금A", "12개월", "인터넷, 영업점", "개인, 법인", "급여 이체 시 우대",
                    1000000L, 100000000L, true, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31),
                    "만기 후 원금과 이자 지급", new BigDecimal("3.5"), new BigDecimal("4.2"), LocalDate.of(2025, 6, 15), "http://example.com/pdf", "http://example.com/image",
                    new BigDecimal("3.0"), new BigDecimal("3.5"), new BigDecimal("4.0")),
            new Deposit(2L, "우리은행", "정기예금B", "24개월", "영업점", "개인", "장기 가입 시 우대",
                    500000L, 50000000L, false, null, null,
                    "만기 후 원금과 이자 지급", new BigDecimal("2.8"), new BigDecimal("3.5"), LocalDate.of(2024, 12, 1), "http://example.com/pdf2", "http://example.com/image2",
                    new BigDecimal("2.5"), new BigDecimal("3.0"), new BigDecimal("3.8")),
            new Deposit(3L, "신한은행", "정기예금C", "36개월", "모바일 앱", "개인, 법인", "급여 이체 및 자동이체 시 우대",
                    2000000L, 200000000L, true, LocalDate.of(2024, 6, 1), LocalDate.of(2025, 6, 30),
                    "만기 후 원금과 이자 지급", new BigDecimal("4.0"), new BigDecimal("5.0"), LocalDate.of(2025, 12, 1), "http://example.com/pdf3", "http://example.com/image3",
                    new BigDecimal("3.8"), new BigDecimal("4.2"), new BigDecimal("4.8")));

    // ✅ 이자율 순 정렬 API (내림차순)
    @Operation(
            summary = "이자율 순 정렬된 예적금 목록 조회",
            description = "예적금 목록을 이자율이 높은 순서대로 정렬하여 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 이자율 순으로 정렬된 목록을 반환함")
    @GetMapping("/sort-by-interest")
    public List<Deposit> getDepositsSortedByInterest() {
        return MOCK_DEPOSITS.stream()
                .sorted(Comparator.comparing(Deposit::getMaxInterestRate).reversed())
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
                .sorted(Comparator.comparing(Deposit::getMaxContractPeriod))
                .collect(Collectors.toList());
    }
}
