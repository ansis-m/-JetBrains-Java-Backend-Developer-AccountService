package account.payslip;

import account.user.Salary;
import account.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class PaySlip {

       @Id
       @GeneratedValue(strategy = GenerationType.AUTO)
       @Column(name = "id", nullable = false)
       @JsonIgnore
       private Long id;

       @ManyToOne(targetEntity = User.class,
               fetch = FetchType.LAZY,
               cascade = {CascadeType.MERGE, CascadeType.REMOVE})
       @NotNull(message = "Please provide user")
       @JsonIgnore
       private User user;

       @Column
       private String name;

       @Column
       private String lastname;

       @Column
       private String period;

       @Column
       @JsonIgnore
       private String date;

       @Column
       private String salary;

       private static final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};


       public PaySlip(Salary salary, User user) {
              this.user = user;
              this.name = user.getName();
              this.lastname = user.getLastname();
              this.date = salary.getPeriod();

              int amount = salary.getSalary();
              this.salary = String.format("%d dollar(s)", amount/100);
              if (amount % 100 >= 0)
                     this.salary += String.format(" %d cent(s)", amount % 100);

              StringBuilder builder = new StringBuilder();
              String[] tokens = salary.getPeriod().split("-");

              try {
                     builder.append(months[Integer.valueOf(tokens[0]) - 1]);
                     builder.append("-");
                     builder.append(tokens[1]);
              }
              catch (Exception e){
                     e.printStackTrace();
              }
              this.period = builder.toString();
       }
}
