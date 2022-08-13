package account;

import account.user.User;
import account.user.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
public class LogRestController {

    @Autowired
    UserServiceImp userServiceImp;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("api/auth/signup")
    public ResponseEntity Signup(@RequestBody User user){

        System.out.println("\n\nREGISTRATION\n");
        System.out.println(user.getName());
        System.out.println(user.getLastname());

        user.setEmail(user.getEmail().toLowerCase());
        System.out.println(user.getEmail());
        System.out.println(user.getPassword() + "\n********************");

        if(user.valid()) {
            if(uniqueEmail(user.getEmail())){
                System.out.println("SAVED!!!\n");
                user.setPassword(encoder.encode(user.getPassword()));
                userServiceImp.save(user);
                User savedUser = userServiceImp.findByEmail(user.getEmail());
                savedUser.setId(savedUser.getNumber().getNumber());
                userServiceImp.save(savedUser);

                return new ResponseEntity(savedUser, HttpStatus.OK);
            }
            else {
                System.out.println("NOT SAVED!!!\n");
                return new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/auth/signup", "message", "User exist!"), HttpStatus.BAD_REQUEST);

            }
        }
        else {
            System.out.println("NOT SAVED!!!\n");
            return new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/auth/signup"), HttpStatus.BAD_REQUEST);
        }

    }


    @GetMapping ("api/empl/payment")
    public ResponseEntity Payment(Authentication auth){


        System.out.println("\n\nPAYMENT\n" + auth.getName());

            User user = userServiceImp.findByEmail(auth.getName());
            if(user != null) {
                System.out.println(user.getName());
                System.out.println(user.getLastname());
                return  new ResponseEntity(user, HttpStatus.OK);
            }
            else
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);

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
