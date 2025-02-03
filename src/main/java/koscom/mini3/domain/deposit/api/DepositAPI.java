package koscom.mini3.domain.deposit.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import koscom.mini3.domain.deposit.domain.Deposit;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "예적금 API", description = "예적금 관련 API")
@RestController
@RequestMapping("/api/deposits")
public class DepositAPI {

    // ✅ Mock 예적금 데이터
    private static final List<Deposit> MOCK_DEPOSITS = Arrays.asList(
            new Deposit(1L, "한국은행", "정기예금A", 6, 24, "인터넷, 영업점", "개인, 법인", "급여 이체 시 우대",
                    1000000L, 100000000L, true, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31),
                    "만기 후 원금과 이자 지급", new BigDecimal("3.5"), new BigDecimal("4.2"),  "http://example.com/pdf", "http://example.com/image",
                    new BigDecimal("3.0"), new BigDecimal("3.5"), new BigDecimal("4.0")),
            new Deposit(2L, "우리은행", "정기예금B", 12, 36, "영업점", "개인", "장기 가입 시 우대",
                    500000L, 50000000L, false, null, null,
                    "만기 후 원금과 이자 지급", new BigDecimal("2.8"), new BigDecimal("3.5"), "http://example.com/pdf2", "http://example.com/image2",
                    new BigDecimal("2.5"), new BigDecimal("3.0"), new BigDecimal("3.8")),
            new Deposit(3L, "신한은행", "정기예금C", 24, 48, "모바일 앱", "개인, 법인", "급여 이체 및 자동이체 시 우대",
                    2000000L, 200000000L, true, LocalDate.of(2024, 6, 1), LocalDate.of(2025, 6, 30),
                    "만기 후 원금과 이자 지급", new BigDecimal("4.0"), new BigDecimal("5.0"), "http://example.com/pdf3", "http://example.com/image3",
                    new BigDecimal("3.8"), new BigDecimal("4.2"), new BigDecimal("4.8")));

    // ✅ 예금 기본금리 높은 순 정렬 API
    @Operation(
            summary = "예금 기본금리 높은 순 정렬",
            description = "가입기간과 최소 가입 금액을 기준으로 필터링 후, 기본 금리가 높은 순서대로 정렬하여 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 기본금리 순으로 정렬된 목록을 반환함")
    @GetMapping("/baserate")
    public List<Deposit> getDepositsSortedByBaseRate(@RequestParam int term, @RequestParam Long min_amount) {
        return MOCK_DEPOSITS.stream()
                .filter(deposit -> deposit.getMinContractPeriod() <= term && deposit.getMaxContractPeriod() >= term && deposit.getMinDepositLimit() >= min_amount)
                .sorted(Comparator.comparing(Deposit::getBaseInterestRate).reversed())
                .collect(Collectors.toList());
    }

    // ✅ 예금 최고금리 높은 순 정렬 API
    @Operation(
            summary = "예금 최고금리 높은 순 정렬",
            description = "가입기간과 최소 가입 금액을 기준으로 필터링 후, 최고 금리가 높은 순서대로 정렬하여 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 최고금리 순으로 정렬된 목록을 반환함")
    @GetMapping("/highrate")
    public List<Deposit> getDepositsSortedByHighRate(@RequestParam int term, @RequestParam Long min_amount) {
        return MOCK_DEPOSITS.stream()
                .filter(deposit -> deposit.getMinContractPeriod() <= term && deposit.getMaxContractPeriod() >= term && deposit.getMinDepositLimit() >= min_amount)
                .sorted(Comparator.comparing(Deposit::getMaxInterestRate).reversed())
                .collect(Collectors.toList());
    }

    // ✅ 예금 단일 조회 API
    @Operation(
            summary = "예금 단일 조회",
            description = "지정된 ID에 해당하는 예금 정보를 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 예금 정보를 반환함")
    @GetMapping("/{id}")
    public Deposit getDepositById(@PathVariable Long id) {
        return MOCK_DEPOSITS.stream()
                .filter(deposit -> deposit.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
