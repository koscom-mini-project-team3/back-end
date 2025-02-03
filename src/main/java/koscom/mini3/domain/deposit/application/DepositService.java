package koscom.mini3.domain.deposit.application;

import koscom.mini3.domain.deposit.domain.Deposit;
import koscom.mini3.domain.deposit.dto.DepositResponseDto;
import koscom.mini3.domain.deposit.dao.DepositRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepositService {
    private final DepositRepository depositRepository;

    public DepositService(DepositRepository depositRepository) {
        this.depositRepository = depositRepository;
    }

    public List<DepositResponseDto> getDepositsSortedByBaseRate(int term, Long minAmount) {
        return depositRepository.findByMinContractPeriodLessThanEqualAndMaxContractPeriodGreaterThanEqualAndMinDepositLimitLessThanEqual(term, term, minAmount)
                .stream()
                .sorted((d1, d2) -> d2.getBaseInterestRate().compareTo(d1.getBaseInterestRate()))
                .map(DepositResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<DepositResponseDto> getDepositsSortedByHighRate(int term, Long minAmount) {
        return depositRepository.findByMinContractPeriodLessThanEqualAndMaxContractPeriodGreaterThanEqualAndMinDepositLimitLessThanEqual(term, term, minAmount)
                .stream()
                .sorted((d1, d2) -> d2.getMaxInterestRate().compareTo(d1.getMaxInterestRate()))
                .map(DepositResponseDto::new)
                .collect(Collectors.toList());
    }

    public DepositResponseDto getDepositById(Long id) {
        return depositRepository.findById(id)
                .map(DepositResponseDto::new)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 예금이 존재하지 않습니다."));
    }
}
