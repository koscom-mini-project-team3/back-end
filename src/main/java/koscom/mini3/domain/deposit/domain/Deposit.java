package koscom.mini3.domain.deposit.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "예적금 정보")
public class Deposit {
    @Schema(description = "예적금 상품명", example = "예금A")
    private String name;

    @Schema(description = "이자율 (%)", example = "3.5")
    private double interestRate;

    @Schema(description = "만기일", example = "2025-01-01")
    private LocalDate maturityDate;

    public Deposit(String name, double interestRate, LocalDate maturityDate) {
        this.name = name;
        this.interestRate = interestRate;
        this.maturityDate = maturityDate;
    }

    public String getName() {
        return name;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }
}
