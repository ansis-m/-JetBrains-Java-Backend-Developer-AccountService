package account.Controllers;

import account.SecurityEvents.EventService;
import account.SecurityEvents.EventServiceImp;
import account.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
public class AuditorRestController {


    @Autowired
    EventService eventService;


    @Secured({"ROLE_AUDITOR"})
    @GetMapping("api/security/events")
    public ResponseEntity deleteUser(@PathVariable(required = false) String email) {

        System.out.println("\n\n*******Inside api/security/events**********\n\n");

        return new ResponseEntity(eventService.getAll(), HttpStatus.OK);

    }
}
