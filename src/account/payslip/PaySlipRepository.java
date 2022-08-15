package account.payslip;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaySlipRepository extends JpaRepository<PaySlip, Long> {
}
