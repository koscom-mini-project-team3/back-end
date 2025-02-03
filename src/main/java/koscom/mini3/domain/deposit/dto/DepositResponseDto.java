package koscom.mini3.domain.deposit.dto;

import koscom.mini3.domain.deposit.domain.Deposit;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class DepositResponseDto {
    private Long id;
    private String bankName;
    private String productName;
    private Integer minContractPeriod;
    private Integer maxContractPeriod;
    private BigDecimal baseInterestRate;
    private BigDecimal maxInterestRate;
    private Long minDepositLimit;
    private String subscriptionMethod;
    private String eligibleCustomers;
    private String specialConditions;
    private boolean isSpecial;
    private String maturityInterest;
    private String pdfUrl;
    private String imageUrl;
    private BigDecimal sixMonthInterestRate;
    private BigDecimal twelveMonthInterestRate;
    private BigDecimal twentyFourMonthInterestRate;

    public DepositResponseDto(Deposit deposit) {
        this.id = deposit.getId();
        this.bankName = deposit.getBankName();
        this.productName = deposit.getProductName();
        this.minContractPeriod = deposit.getMinContractPeriod();
        this.maxContractPeriod = deposit.getMaxContractPeriod();
        this.baseInterestRate = deposit.getBaseInterestRate();
        this.maxInterestRate = deposit.getMaxInterestRate();
        this.minDepositLimit = deposit.getMinDepositLimit();
        this.subscriptionMethod = deposit.getSubscriptionMethod();
        this.eligibleCustomers = deposit.getEligibleCustomers();
        this.specialConditions = deposit.getSpecialConditions();
        this.isSpecial = deposit.isSpecial();
        this.maturityInterest = deposit.getMaturityInterest();
        this.pdfUrl = deposit.getPdfUrl();
        this.imageUrl = deposit.getImageUrl();
        this.sixMonthInterestRate = deposit.getSixMonthInterestRate();
        this.twelveMonthInterestRate = deposit.getTwelveMonthInterestRate();
        this.twentyFourMonthInterestRate = deposit.getTwentyFourMonthInterestRate();
    }
}
