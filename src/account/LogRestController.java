package account;

import account.payslip.PaySlipServiceImp;
import account.securityConfig.pCheck;
import account.payslip.PaySlip;
import account.user.Salary;
import account.user.User;
import account.user.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class LogRestController {

    @Autowired
    UserServiceImp userServiceImp;

    @Autowired
    PaySlipServiceImp paySlipServiceImp;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("api/auth/signup")
    public ResponseEntity Signup(@RequestBody User user){

        String message = "User exist!";

        System.out.println("\n\nREGISTRATION\n");
        System.out.println(user.getName());
        System.out.println(user.getLastname());

        System.out.println(user.getEmail());
        System.out.println(user.getPassword() + "\n********************");

        if(user.valid()) {
            user.setEmail(user.getEmail().toLowerCase());
            if(uniqueEmail(user.getEmail()) && pCheck.isValid(user.getPassword())){
                System.out.println("SAVED!!!\n");
                user.setPassword(encoder.encode(user.getPassword()));
                userServiceImp.save(user);
                User savedUser = userServiceImp.findByEmail(user.getEmail());
                savedUser.setId(savedUser.getNumber().getNumber());
                userServiceImp.save(savedUser);

                return new ResponseEntity(savedUser, HttpStatus.OK);
            }
            else if (user.getPassword().length() < 12){
                message = "The password length must be at least 12 chars!";
            }
            else if (!pCheck.isValid(user.getPassword()))
                message = "The password is in the hacker's database!";
        }
        return new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/auth/signup", "message", message), HttpStatus.BAD_REQUEST);

    }


    @PostMapping("api/auth/changepass")
    public ResponseEntity ChangePassword(@RequestBody Map<String, String> password, Authentication auth){

        System.out.println("\nChangePassword\n\n");
        String message = "message";
        System.out.println(password.get("new_password"));

        System.out.println(auth.getName());
        User user = userServiceImp.findByEmail(auth.getName());

        System.out.println("\n******************\n\n");

        if (password.get("new_password") == null || password.get("new_password").length() < 12) {
            message = "Password length must be 12 chars minimum!";
            return new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/auth/changepass", "message", message), HttpStatus.BAD_REQUEST);
        }
        else if (encoder.matches(password.get("new_password"), user.getPassword())) {
            message = "The passwords must be different!";
            return new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/auth/changepass", "message", message), HttpStatus.BAD_REQUEST);
        }
        else if (!pCheck.isValid(password.get("new_password"))) {
            message = "The password is in the hacker's database!";
            return new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/auth/changepass", "message", message), HttpStatus.BAD_REQUEST);
        }
        else {
            user.setPassword(encoder.encode(password.get("new_password")));
            userServiceImp.save(user);
            System.out.println("******************");
            return new ResponseEntity(Map.of("email", user.getEmail(), "status", "The password has been updated successfully"), HttpStatus.OK);

        }

    }


    @GetMapping ("api/empl/payment")
    public ResponseEntity Payment(@RequestParam (required=false) String period, Authentication auth){


        System.out.println("\n\n******PAYMENT********\n" + auth.getName());

        if(period != null)
            System.out.println("Period: " + period + "\n");

            User user = userServiceImp.findByEmail(auth.getName());
            if(user != null) {
                System.out.println(user.getName());
                System.out.println(user.getLastname());
                ArrayList<PaySlip> payslips = new ArrayList<>(user.getPaySlips());
                Collections.reverse(payslips);
                if(period == null)
                    return  new ResponseEntity(payslips, HttpStatus.OK);
                else {
                    for(PaySlip p : payslips){
                        System.out.println("period and date: " + period + "  " + p.getDate());
                        if(p.getDate().equals(period))
                            return  new ResponseEntity(p, HttpStatus.OK);
                    }
                }
                return  new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/empl/payment", "message", "message"), HttpStatus.BAD_REQUEST);
            }
            else
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PostMapping ("api/acct/payments")
    public ResponseEntity Payments(@RequestBody ArrayList<Salary> salary){

        System.out.println("INSIDE api/acct/payments");
        for(Salary s : salary) {
            System.out.println(s.getPeriod() + "   " + s.getEmployee() + "  " + s.getSalary());
        }

        String message = Salary.parsePayments(salary);
        System.out.println("message: " + message);
        if (message.length() > 5)
            return new ResponseEntity(Map.of("timestamp",LocalDate.now(), "error", "Bad Request", "path", "/api/acct/payments", "message", message, "status", 400), HttpStatus.BAD_REQUEST);

        for(Salary s : salary) {
            try{
                System.out.println("email?:  " + s.getEmployee());
                User user = userServiceImp.findByEmail(s.getEmployee());
                if(user == null) {
                    System.out.println("No such user found!!!!");
                    return new ResponseEntity(Map.of("timestamp",LocalDate.now(), "error", "Bad Request", "path", "/api/acct/payments", "message", "no such user", "status", 400), HttpStatus.BAD_REQUEST);
                }

                PaySlip paySlip = new PaySlip(s, user);
                if (user.getMonths().contains(paySlip.getPeriod()))
                    return new ResponseEntity(Map.of("timestamp",LocalDate.now(), "error", "Bad Request", "path", "/api/acct/payments", "message", "Duplicated entry in payment list", "status", 400), HttpStatus.BAD_REQUEST);
                paySlipServiceImp.save(paySlip);

                System.out.println("\n****PRINTING PAYSLIP****\n\n");
                System.out.println(paySlip.getName());
                System.out.println(paySlip.getLastname());
                System.out.println(paySlip.getPeriod());
                System.out.println(paySlip.getSalary());
                System.out.println("***************************\n");
                user.addPayslip(paySlip);
                user.addMonth(paySlip.getPeriod());
                userServiceImp.save(user);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new ResponseEntity(Map.of("status", "Added successfully!"), HttpStatus.OK);
    }


    @PutMapping ("api/acct/payments")
    public ResponseEntity EditPayments(@RequestBody Salary s){

        System.out.println("\n\nINSIDE PUT api/acct/payments\n\n");

        String error = Salary.parsePayments(s);
        User user = userServiceImp.findByEmail(s.getEmployee());

        if(error.length() > 0 || user == null) {
            return new ResponseEntity(Map.of("timestamp",LocalDate.now(), "error", "Bad Request", "path", "/api/acct/payments", "message", error, "status", 400), HttpStatus.BAD_REQUEST);
        }
        PaySlip newPaySlip = new PaySlip(s, user);

        List<PaySlip> allPaySlips = user.getPaySlips();
        for(PaySlip p : allPaySlips) {
            if(p.getPeriod().equals(newPaySlip.getPeriod())) {
                newPaySlip.setId(p.getId());
                paySlipServiceImp.save(newPaySlip);
                return new ResponseEntity(Map.of("status", "Updated successfully!"), HttpStatus.OK);

            }
        }
        user.addPayslip(newPaySlip);
        paySlipServiceImp.save(newPaySlip);
        userServiceImp.save(user);
        return new ResponseEntity(Map.of("status", "Updated successfully!"), HttpStatus.OK);
    }

    private boolean uniqueEmail(String email) {
        return !userServiceImp.exists(email);
    }


    @GetMapping ("api/get")
    public ResponseEntity Signup(){

        return new ResponseEntity(userServiceImp.getAll(), HttpStatus.OK);

    }

    @GetMapping ("api/delete/all")
    public ResponseEntity Clean(){

        userServiceImp.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
