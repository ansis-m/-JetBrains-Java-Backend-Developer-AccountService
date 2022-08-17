package account;

import account.payslip.PaySlip;
import account.payslip.PaySlipServiceImp;
import account.securityConfig.pCheck;
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
public class UserRestController {

    @Autowired
    UserServiceImp userServiceImp;

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


    private boolean uniqueEmail(String email) {
        return !userServiceImp.exists(email);
    }

}
