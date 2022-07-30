package account;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
public class LogRestController {


    @PostMapping("api/auth/signup")
    public ResponseEntity Signup(@RequestBody Form form){
        System.out.println(form.getName());
        System.out.println(form.getLastname());

        if(form.valid())
            return new ResponseEntity(form, HttpStatus.OK);
        else
            return new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/auth/signup"), HttpStatus.BAD_REQUEST);

    }


}
