package account;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
public class LogRestController {

    @Autowired
    UserServiceImp userServiceImp;


    @PostMapping("api/auth/signup")
    public ResponseEntity Signup(@RequestBody User user){
        System.out.println(user.getName());
        System.out.println(user.getLastname());

        if(user.valid()) {
            userServiceImp.save(user);
            return new ResponseEntity(user, HttpStatus.OK);
        }

        else
            return new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/auth/signup"), HttpStatus.BAD_REQUEST);

    }


    @PostMapping("api/get")
    public ResponseEntity Signup(){

        return new ResponseEntity(userServiceImp.getAll(), HttpStatus.OK);

    }


}
