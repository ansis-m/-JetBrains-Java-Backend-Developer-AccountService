package account;

import account.user.User;
import account.user.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
public class AdminRestController {

    @Autowired
    UserServiceImp userServiceImp;

    @Autowired
    PasswordEncoder encoder;

    @Secured({"ROLE_ADMINISTRATOR"})
    @GetMapping ("api/admin/user")
    public ResponseEntity users(){

        return new ResponseEntity(userServiceImp.getAll(), HttpStatus.OK);

    }

    @Secured({"ROLE_ADMINISTRATOR"})
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

    @Secured({"ROLE_ADMINISTRATOR"})
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

    @GetMapping ("api/get")
    public ResponseEntity GetAll(){

        return new ResponseEntity(userServiceImp.getAll(), HttpStatus.OK);

    }
}
