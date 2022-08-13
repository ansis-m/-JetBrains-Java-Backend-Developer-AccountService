package account.numberService;

import account.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="id")
public class GeneralSequenceNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long number;

    public GeneralSequenceNumber(Long number) {
        this.number = number;
    }

    @OneToOne(mappedBy = "number")
    private User user;
}
