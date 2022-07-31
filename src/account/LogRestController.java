package account;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

@RestController
public class LogRestController {

    @Autowired
    UserServiceImp userServiceImp;

    @Autowired
    NumberServiceImp numberServiceImp;

    @PostMapping("api/auth/signup")
    public ResponseEntity Signup(@RequestBody User user){
        System.out.println(user.getName());
        System.out.println(user.getLastname());

        user.setEmail(user.getEmail().toLowerCase());

        if(user.valid()) {
            if(uniqueEmail(user.getEmail())){

                userServiceImp.save(user);
                User savedUser = userServiceImp.findByEmail(user.getEmail());
                savedUser.setId(savedUser.getNumber().getNumber());

                return new ResponseEntity(savedUser, HttpStatus.OK);
            }
            else
                return new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/auth/signup", "message", "User exist!"), HttpStatus.BAD_REQUEST);
        }

        else
            return new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/auth/signup"), HttpStatus.BAD_REQUEST);

    }

    private boolean uniqueEmail(String email) {
        return !userServiceImp.exists(email);
    }


    @PostMapping("api/get")
    public ResponseEntity Signup(){

        return new ResponseEntity(userServiceImp.getAll(), HttpStatus.OK);

    }


}
