package account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NumberRepository extends JpaRepository<GeneralSequenceNumber, Long> {
}
