package koscom.mini3.domain.deposit.dao;

import koscom.mini3.domain.deposit.domain.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {
    List<Deposit> findByMinContractPeriodLessThanEqualAndMaxContractPeriodGreaterThanEqualAndMinDepositLimitLessThanEqual(
            int minTerm, int maxTerm, Long minAmount);
}
