package koscom.mini3.domain.deposit.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Schema(description = "예적금 정보")
public class Deposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "예적금 ID", example = "1")
    private Long id;

    @Column(length = 100, nullable = false)
    @Schema(description = "은행명", example = "한국은행")
    private String bankName;

    @Column(length = 200, nullable = false)
    @Schema(description = "예적금 상품명", example = "정기예금A")
    private String productName;

    @Schema(description = "최소 가입 기간 (개월)", example = "6")
    private Integer minContractPeriod;

    @Schema(description = "최대 가입 기간 (개월)", example = "24")
    private Integer maxContractPeriod;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "가입 방법", example = "인터넷, 영업점")
    private String subscriptionMethod;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "가입 대상", example = "개인, 법인")
    private String eligibleCustomers;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "우대 조건", example = "급여 이체 시 우대")
    private String specialConditions;

    @Schema(description = "최소 가입 금액", example = "1000000")
    private Long minDepositLimit;

    @Schema(description = "최대 가입 금액", example = "100000000")
    private Long maxDepositLimit;

    @Schema(description = "우대 여부", example = "true")
    private boolean isSpecial;

    @Schema(description = "우대 적용 시작일", example = "2024-01-01")
    private LocalDate specialStartDate;

    @Schema(description = "우대 적용 종료일", example = "2024-12-31")
    private LocalDate specialEndDate;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "만기 시 이자 지급 방법", example = "일시 지급")
    private String maturityInterest;

    @Schema(description = "기본 이자율 (%)", example = "3.50")
    private BigDecimal baseInterestRate;

    @Schema(description = "최대 이자율 (%)", example = "5.00")
    private BigDecimal maxInterestRate;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "상품 설명서 PDF URL", example = "https://example.com/deposit.pdf")
    private String pdfUrl;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "상품 이미지 URL", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "6개월 이자율 (%)", example = "3.00")
    private BigDecimal sixMonthInterestRate;

    @Schema(description = "12개월 이자율 (%)", example = "3.50")
    private BigDecimal twelveMonthInterestRate;

    @Schema(description = "24개월 이자율 (%)", example = "4.00")
    private BigDecimal twentyFourMonthInterestRate;

    @Schema(description = "pdf 전문", example = "어쩌구저쩌구")
    private String pdfString;
}