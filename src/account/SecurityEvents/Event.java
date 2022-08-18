package account.SecurityEvents;


import account.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "event")
@Getter
@Setter
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private String date;

    @Column
    private String action;

    @Column
    private String subject;

    @Column
    private String object;

    @Column
    private String path;

    public Event(String action, String subject, String object, String path) {
        this.date = LocalDate.now().toString();
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }

}
