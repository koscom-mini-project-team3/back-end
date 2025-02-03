package koscom.mini3.domain.deposit.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import koscom.mini3.domain.deposit.dto.DepositResponseDto;
import koscom.mini3.domain.deposit.application.DepositService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "예적금 API", description = "예적금 관련 API")
@RestController
@RequestMapping("/api/deposits")
public class DepositAPI {

    private final DepositService depositService;

    public DepositAPI(DepositService depositService) {
        this.depositService = depositService;
    }

    // ✅ 예금 기본금리 높은 순 정렬 API
    @Operation(
            summary = "예금 기본금리 높은 순 정렬",
            description = "가입기간과 최소 가입 금액을 기준으로 필터링 후, 기본 금리가 높은 순서대로 정렬하여 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 기본금리 순으로 정렬된 목록을 반환함")
    @GetMapping("/baserate")
    public List<DepositResponseDto> getDepositsSortedByBaseRate(@RequestParam(name = "term",defaultValue = "0") int term, @RequestParam(name = "minAmount",defaultValue = "0") Long minAmount)  {
        return depositService.getDepositsSortedByBaseRate(term, minAmount);
    }

    // ✅ 예금 최고금리 높은 순 정렬 API
    @Operation(
            summary = "예금 최고금리 높은 순 정렬",
            description = "가입기간과 최소 가입 금액을 기준으로 필터링 후, 최고 금리가 높은 순서대로 정렬하여 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 최고금리 순으로 정렬된 목록을 반환함")
    @GetMapping("/highrate")
    public List<DepositResponseDto> getDepositsSortedByHighRate(@RequestParam(name = "term",defaultValue = "0") int term, @RequestParam(name = "minAmount",defaultValue = "0") Long minAmount)  {
        return depositService.getDepositsSortedByHighRate(term, minAmount);
    }

    // ✅ 예금 단일 조회 API
    @Operation(
            summary = "예금 단일 조회",
            description = "지정된 ID에 해당하는 예금 정보를 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 예금 정보를 반환함")
    @GetMapping("/{id}")
    public DepositResponseDto getDepositById(@PathVariable("id") Long id) {
        return depositService.getDepositById(id);
    }
}
