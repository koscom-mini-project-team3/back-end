package koscom.mini3.domain.deposit.application;

import koscom.mini3.domain.deposit.dao.DepositRepository;
import koscom.mini3.domain.deposit.domain.Deposit;
import koscom.mini3.domain.deposit.dto.DepositResponseDto;
import koscom.mini3.deposit.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepositServiceTest {

    @Mock
    private DepositRepository depositRepository;

    @InjectMocks
    private DepositService depositService;

    private List<Deposit> mockDeposits;

    @BeforeEach
    void setUp() {
        mockDeposits = TestDataUtil.createMockDeposits();
    }

    @Test
    void getDepositsSortedByBaseRate_ShouldReturnSortedDeposits() {
        when(depositRepository.findByMinContractPeriodLessThanEqualAndMaxContractPeriodGreaterThanEqualAndMinDepositLimitLessThanEqual(anyInt(), anyInt(), anyLong()))
                .thenReturn(mockDeposits);

        List<DepositResponseDto> result = depositService.getDepositsSortedByBaseRate(12, 1000000L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getBaseInterestRate().compareTo(result.get(1).getBaseInterestRate()) >= 0);
    }

    @Test
    void getDepositsSortedByHighRate_ShouldReturnSortedDeposits() {
        when(depositRepository.findByMinContractPeriodLessThanEqualAndMaxContractPeriodGreaterThanEqualAndMinDepositLimitLessThanEqual(anyInt(), anyInt(), anyLong()))
                .thenReturn(mockDeposits);

        List<DepositResponseDto> result = depositService.getDepositsSortedByHighRate(12, 1000000L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getMaxInterestRate().compareTo(result.get(1).getMaxInterestRate()) >= 0);
    }

    @Test
    void getDepositById_ShouldReturnDeposit() {
        when(depositRepository.findById(1L)).thenReturn(Optional.of(mockDeposits.get(0)));

        DepositResponseDto result = depositService.getDepositById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getDepositById_ShouldThrowExceptionWhenNotFound() {
        when(depositRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> depositService.getDepositById(999L));
    }
}
