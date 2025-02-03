package koscom.mini3.deposit.util;

import koscom.mini3.domain.deposit.domain.Deposit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TestDataUtil {
    public static List<Deposit> createMockDeposits() {
        return List.of(
                new Deposit(1L, "한국은행", "정기예금A", 6, 24, "인터넷, 영업점", "개인, 법인", "급여 이체 시 우대",
                        1000000L, 100000000L, true, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31),
                        "만기 후 원금과 이자 지급", new BigDecimal("3.50"), new BigDecimal("5.00"),
                        "https://example.com/deposit1.pdf", "https://example.com/image1.jpg",
                        new BigDecimal("3.00"), new BigDecimal("3.50"), new BigDecimal("4.00")),

                new Deposit(2L, "우리은행", "정기예금B", 12, 36, "영업점", "개인", "장기 가입 시 우대",
                        500000L, 50000000L, false, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 12, 31),
                        "만기 후 원금과 이자 지급", new BigDecimal("2.80"), new BigDecimal("3.80"),
                        "https://example.com/deposit2.pdf", "https://example.com/image2.jpg",
                        new BigDecimal("2.50"), new BigDecimal("3.00"), new BigDecimal("3.80")),

                new Deposit(3L, "신한은행", "정기예금C", 24, 48, "모바일 앱", "개인, 법인", "자동이체 시 우대",
                        2000000L, 200000000L, true, LocalDate.of(2024, 6, 1), LocalDate.of(2025, 6, 30),
                        "만기 후 원금과 이자 지급", new BigDecimal("4.00"), new BigDecimal("5.50"),
                        "https://example.com/deposit3.pdf", "https://example.com/image3.jpg",
                        new BigDecimal("3.80"), new BigDecimal("4.20"), new BigDecimal("4.80"))
        );
    }
}
