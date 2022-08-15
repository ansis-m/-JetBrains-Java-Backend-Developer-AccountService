package account.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class Salary {

    private String employee;
    private String period;
    private int salary;

    public Salary(String email, String period, int salary) {
        this.employee = email;
        this.period = period;
        this.salary = salary;
    }

    public static String parsePayments(ArrayList<Salary> salary){

        StringBuilder result = new StringBuilder();
        boolean comma = false;

        for(int i = 0; i < salary.size(); i++) {
            if(salary.get(i).getSalary() < 0) {
                if(comma)
                    result.append(", ");
                comma = true;
                result.append(String.format("payments[%d].salary: Salary must be non negative!", i));
            }
            if(!validDate(salary.get(i).getPeriod())) {
                if(comma)
                    result.append(", ");
                comma = true;
                result.append(String.format("payments[%d].period: Wrong date!", i));
            }
        }
        return result.toString();
    }

    public static String parsePayments(Salary salary){

        StringBuilder result = new StringBuilder();
        boolean comma = false;


            if(salary.getSalary() < 0) {
                comma = true;
                result.append("payments.salary: Salary must be non negative!");
            }
            if(!validDate(salary.getPeriod())) {
                if(comma)
                    result.append(", ");
                result.append("payments.period: Wrong date!");
            }

        return result.toString();
    }

    private static boolean validDate(String period) {
        String[] tokens = period.split("-");
        if(tokens.length != 2)
            return false;
        try{
            int m = Integer.valueOf(tokens[0]);
            if(m < 1 || m > 12)
                return false;
            int y = Integer.valueOf(tokens[1]);
            if (y < 1900 || y > 2023)
                return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
