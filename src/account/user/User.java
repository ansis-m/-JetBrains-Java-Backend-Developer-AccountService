package account.user;

import account.numberService.GeneralSequenceNumber;
import account.payslip.PaySlip;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name="User")
public class User {


    @Id
    @Column(name = "email", nullable = false)
    private String email;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "IDd", referencedColumnName = "number")
    @JsonIgnore
    private GeneralSequenceNumber number;

    @Column(length = 30)
    private String name;

    @JsonInclude()
    Long id;

    @Column(length = 30)
    private String lastname;

    @Column
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<PaySlip> paySlips;


    @JsonIgnore
    @Basic
    @Size(max=2500)
    private ArrayList<String> months;


    public User(){
        months = new ArrayList<String>();
        paySlips = new ArrayList<PaySlip>();
        number = new GeneralSequenceNumber();
    }

    public User(String name, String lastname, String email, String password, ArrayList<PaySlip> paySlips, ArrayList<String> months) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.paySlips = paySlips;
        number = new GeneralSequenceNumber();
        this.months = months;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setId() {
        this.id = number.getNumber();
    }

    public void addPayslip(PaySlip paySlip) {
        paySlips.add(paySlip);
    }

    public void addMonth(String month) {
        months.add(month);
    }


    public boolean valid() {
        return name != null && lastname != null &&
                email != null && password != null &&
                name.length() > 0 && lastname.length() > 0 &&
                password.length() > 0 && email.endsWith("@acme.com");
    }

}
