package account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;


@Getter
@Setter
@Entity
@Table(name="User")
public class User {


    @Id
    @Column(name = "email", nullable = false)
    private String email;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID", referencedColumnName = "number")
    @JsonIgnore
    private GeneralSequenceNumber number;

    @Column(length = 30)
    private String name;

    @JsonInclude()
    @Transient
    Long id;

    @Column(length = 30)
    private String lastname;

    @Column
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;


    public User() {
        number = new GeneralSequenceNumber();
    }

    public User(String name, String lastname, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        number = new GeneralSequenceNumber();
    }

    public boolean valid() {
        return name != null && lastname != null &&
                email != null && password != null &&
                name.length() > 0 && lastname.length() > 0 &&
                password.length() > 0 && email.endsWith("@acme.com");
    }
}
