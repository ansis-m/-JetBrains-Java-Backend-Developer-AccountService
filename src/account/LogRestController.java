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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

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
                long i = userServiceImp.getCount();
                if(i == 0)
                    user.addRole("ROLE_ADMINISTRATOR");
                else
                    user.addRole("ROLE_USER");
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
        String message;
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
            return new ResponseEntity(Map.of("email", user.getEmail(), "status", "The password has been updated successfully"), HttpStatus.OK);
        }
    }

    @Secured({ "ROLE_USER", "ROLE_ACCOUNTANT"})
    @GetMapping ("api/empl/payment")
    public ResponseEntity Payment(@RequestParam (required=false) String period, Authentication auth){


        System.out.println("\n\n******PAYMENT********\n" + auth.getName());

        User user = userServiceImp.findByEmail(auth.getName());
        if(user != null) {
            ArrayList<PaySlip> payslips = new ArrayList<>(user.getPaySlips());
            Collections.reverse(payslips);
            if(period == null)
                return  new ResponseEntity(payslips, HttpStatus.OK);
            else {
                for(PaySlip p : payslips){
                    if(p.getDate().equals(period))
                        return new ResponseEntity(p, HttpStatus.OK);
                }
            }
            return  new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/empl/payment", "message", "message"), HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @Secured({"ROLE_ACCOUNTANT"})
    @PostMapping ("api/acct/payments")
    public ResponseEntity Payments(@RequestBody (required = false) ArrayList<Salary> salary){

        System.out.println("\n\n********INSIDE POST api/acct/payments*******\n\n");

        if(salary == null)
            System.out.println("\n\ns == null\n\n");

        String message = Salary.parsePayments(salary);
        if (message.length() > 5)
            return new ResponseEntity(Map.of("timestamp",LocalDate.now(), "error", "Bad Request", "path", "/api/acct/payments", "message", message, "status", 400), HttpStatus.BAD_REQUEST);

        for(Salary s : salary) {
            try{
                User user = userServiceImp.findByEmail(s.getEmployee());
                if(user == null) {
                    System.out.println("No such user found!!!!");
                    return new ResponseEntity(Map.of("timestamp",LocalDate.now(), "error", "Bad Request", "path", "/api/acct/payments", "message", "no such user", "status", 400), HttpStatus.BAD_REQUEST);
                }

                PaySlip paySlip = new PaySlip(s, user);
                if (user.getMonths().contains(paySlip.getPeriod()))
                    return new ResponseEntity(Map.of("timestamp",LocalDate.now(), "error", "Bad Request", "path", "/api/acct/payments", "message", "Duplicated entry in payment list", "status", 400), HttpStatus.BAD_REQUEST);
                paySlipServiceImp.save(paySlip);

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


    @Secured({"ROLE_ACCOUNTANT"})
    @PutMapping ("api/acct/payments")
    public ResponseEntity EditPayments(@RequestBody (required = false) Salary s){

        System.out.println("\n\n****INSIDE PUT api/acct/payments******\n\n");

        if(s == null)
            System.out.println("\n\ns == null\n\n");

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
    public ResponseEntity GetAll(){

        return new ResponseEntity(userServiceImp.getAll(), HttpStatus.OK);

    }

    @Secured({ "ROLE_ADMINISTRATOR"})
    @GetMapping ("api/admin/user")
    public ResponseEntity users(){

        return new ResponseEntity(userServiceImp.getAll(), HttpStatus.OK);

    }

    @Secured({ "ROLE_ADMINISTRATOR"})
    @DeleteMapping(value = {"api/admin/user/{email}", "api/admin/user"})
    public ResponseEntity deleteUser(@PathVariable(required = false) String email){

        System.out.println("\n\n*******Inside api/admin/user/{email}\n\n");

        if(userServiceImp.exists(email)) {

            User user = userServiceImp.findByEmail(email);
            if(user.getRoles().contains("ROLE_ADMINISTRATOR"))
                return new ResponseEntity(Map.of("timestamp",LocalDate.now(), "error", "Bad Request", "path", "/api/admin/user/" + email, "message", "Can't remove ADMINISTRATOR role!", "status", 400), HttpStatus.BAD_REQUEST);
            userServiceImp.deleteByEmail(email);
            return new ResponseEntity(Map.of("user", email, "status", "Deleted successfully!"), HttpStatus.OK);
        }
        else
            return new ResponseEntity(Map.of("timestamp",LocalDate.now(), "error", "Not Found", "path", "/api/admin/user/" + email, "message", "User not found!", "status", 404), HttpStatus.NOT_FOUND);
    }

    @Secured({ "ROLE_ADMINISTRATOR"})
    @PutMapping ("api/admin/user/role")
    public ResponseEntity addRole(@RequestBody (required = false) Map<String, String> instructions){


        System.out.println("\n********api/admin/user/role***********\n\n");
        if(instructions == null)
            System.out.println("\nNO REQUEST BODY\n");

        System.out.println(instructions.get("user"));
        System.out.println(instructions.get("role"));
        System.out.println(instructions.get("operation"));

        Map response = new HashMap<>();
        response.put("timestamp", LocalDate.now());
        response.put("path", "/api/admin/user/role");

        User user = userServiceImp.findByEmail(instructions.get("user").toLowerCase());
        if (user == null) {
            response.put("error", "Not Found");
            response.put("status", 404);
            response.put("message", "User not found!");
            return new ResponseEntity(response, HttpStatus.NOT_FOUND);
        }

        if (!instructions.get("role").equals("USER") && !instructions.get("role").equals("ADMINISTRATOR") && !instructions.get("role").equals("ACCOUNTANT")) {
            response.put("error", "Not Found");
            response.put("status", 404);
            response.put("message", "Role not found!");
            return new ResponseEntity(response, HttpStatus.NOT_FOUND);
        }

        if(instructions.get("operation").equals("GRANT")) {
            if(!user.getRoles().contains("ROLE_" + instructions.get("role"))) {
                user.addRole("ROLE_" + instructions.get("role"));
                if(user.getRoles().contains("ROLE_ADMINISTRATOR") && user.getRoles().size() > 1) {
                    response.put("error", "Bad Request");
                    response.put("status", 400);
                    response.put("message", "The user cannot combine administrative and business roles!");
                    return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
                }
                user.sortRoles();
                userServiceImp.save(user);
            }
            return new ResponseEntity(user, HttpStatus.OK);
        }
        else if (instructions.get("operation").equals("REMOVE")) {
            if(!user.getRoles().contains("ROLE_" + instructions.get("role"))) {
                response.put("error", "Bad Request");
                response.put("status", 400);
                response.put("message", "The user does not have a role!");
                return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
            }
            else if (instructions.get("role").equals("ADMINISTRATOR")) {
                response.put("error", "Bad Request");
                response.put("status", 400);
                response.put("message", "Can't remove ADMINISTRATOR role!");
                return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
            }
            else if(user.getRoles().size() == 1) {
                response.put("error", "Bad Request");
                response.put("status", 400);
                response.put("message", "The user must have at least one role!");
                return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
            }
            else {
                user.getRoles().remove("ROLE_" + instructions.get("role"));
                user.sortRoles();
                userServiceImp.save(user);
                return new ResponseEntity(user, HttpStatus.OK);
            }
        }


        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping ("api/delete/all")
    public ResponseEntity Clean(){

        userServiceImp.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    @Secured({ "ROLE_ADMINISTRATOR"})
    @GetMapping ("api/admin")
    public ResponseEntity Hello(Authentication auth){

        System.out.println("authorities: " + auth.getAuthorities());
        System.out.println("credentials: " + auth.getCredentials());
        return new ResponseEntity(Map.of("hello", "admin"), HttpStatus.OK);
    }
}
