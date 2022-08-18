package account.Controllers;

import account.SecurityEvents.Event;
import account.SecurityEvents.EventService;
import account.user.User;
import account.user.UserService;
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
public class AdminRestController {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    EventService eventService;


    @Secured({"ROLE_ADMINISTRATOR"})
    @PutMapping ("api/admin/user/access")
    public ResponseEntity lock(@RequestBody Map<String, String> account, Authentication auth){

//        System.out.println("\n\n*******Inside api/admin/user/access******\n");
//
//        System.out.println("user:  " + account.get("user"));
//        System.out.println("operation: " + account.get("operation"));
//
//        System.out.println("*********************");


        try{
            User user = userService.findByEmail(account.get("user").toLowerCase());
            if(user == null) {
                return new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/admin/user/access", "message", "User not found!"), HttpStatus.BAD_REQUEST);
            }
            if(user.getRoles().contains("ROLE_ADMINISTRATOR")) {
                return new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/admin/user/access", "message", "Can't lock the ADMINISTRATOR!"), HttpStatus.BAD_REQUEST);
            }
            else if (account.get("operation").equals("LOCK")){
                user.setActive(false);
                userService.save(user);
                Event event = new Event("LOCK_USER", auth.getName(), "Lock user " + user.getEmail(), "/api/admin/user/access");
                eventService.save(event);
                return new ResponseEntity(Map.of("status", "User " + user.getEmail() + " locked!"), HttpStatus.OK);
            }
            else if (account.get("operation").equals("UNLOCK")){
                user.setActive(true);
                user.setFailedAttempts(0);
                userService.save(user);
                Event event = new Event("UNLOCK_USER", auth.getName(), "Unlock user " + user.getEmail(), "/api/admin/user/access");
                eventService.save(event);
                return new ResponseEntity(Map.of("status", "User " + user.getEmail() + " unlocked!"), HttpStatus.OK);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/admin/user/access", "message", "Bad request body!"), HttpStatus.BAD_REQUEST);
    }


    @Secured({"ROLE_ADMINISTRATOR"})
    @GetMapping ("api/admin/user")
    public ResponseEntity users(){

        return new ResponseEntity(userService.getAll(), HttpStatus.OK);

    }

    @Secured({"ROLE_ADMINISTRATOR"})
    @DeleteMapping(value = {"api/admin/user/{email}", "api/admin/user"})
    public ResponseEntity deleteUser(@PathVariable(required = false) String email, Authentication auth){

        System.out.println("\n\n*******Inside api/admin/user/{email}\n\n");

        if(userService.exists(email)) {

            User user = userService.findByEmail(email);
            if(user.getRoles().contains("ROLE_ADMINISTRATOR"))
                return new ResponseEntity(Map.of("timestamp",LocalDate.now(), "error", "Bad Request", "path", "/api/admin/user/" + email, "message", "Can't remove ADMINISTRATOR role!", "status", 400), HttpStatus.BAD_REQUEST);
            userService.deleteByEmail(email);
            Event event = new Event("DELETE_USER", auth.getName(),user.getEmail(), "/api/admin/user");
            eventService.save(event);
            return new ResponseEntity(Map.of("user", email, "status", "Deleted successfully!"), HttpStatus.OK);
        }
        else
            return new ResponseEntity(Map.of("timestamp",LocalDate.now(), "error", "Not Found", "path", "/api/admin/user/" + email, "message", "User not found!", "status", 404), HttpStatus.NOT_FOUND);
    }

    @Secured({"ROLE_ADMINISTRATOR"})
    @PutMapping ("api/admin/user/role")
    public ResponseEntity addRole(@RequestBody (required = false) Map<String, String> instructions, Authentication auth){


//        System.out.println("\n********api/admin/user/role***********\n\n");
//        if(instructions == null)
//            System.out.println("\nNO REQUEST BODY\n");
//
//        System.out.println(instructions.get("user"));
//        System.out.println(instructions.get("role"));
//        System.out.println(instructions.get("operation"));

        Map response = new HashMap<>();
        response.put("timestamp", LocalDate.now());
        response.put("path", "/api/admin/user/role");

        User user = userService.findByEmail(instructions.get("user").toLowerCase());
        if (user == null) {
            response.put("error", "Not Found");
            response.put("status", 404);
            response.put("message", "User not found!");
            return new ResponseEntity(response, HttpStatus.NOT_FOUND);
        }

        if (!instructions.get("role").equals("USER") && !instructions.get("role").equals("ADMINISTRATOR") && !instructions.get("role").equals("ACCOUNTANT") && !instructions.get("role").equals("AUDITOR")) {
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
                userService.save(user);
            }
            Event event = new Event("GRANT_ROLE", auth.getName(), "Grant role " + instructions.get("role") + " to " + user.getEmail(), "/api/admin/user/role");
            eventService.save(event);
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
                userService.save(user);
                Event event = new Event("REMOVE_ROLE", auth.getName(), "Remove role " + instructions.get("role") + " from " + user.getEmail(), "/api/admin/user/role");
                eventService.save(event);
                return new ResponseEntity(user, HttpStatus.OK);
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping ("api/delete/all")
    public ResponseEntity Clean(){

        userService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping ("api/get")
    public ResponseEntity GetAll(){

        return new ResponseEntity(userService.getAll(), HttpStatus.OK);

    }
}
