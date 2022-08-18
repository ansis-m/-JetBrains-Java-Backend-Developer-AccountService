package account.securityConfig.AuthenticationListeners;

import account.SecurityEvents.Event;
import account.SecurityEvents.EventService;
import account.user.User;
import account.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

@Component
public class AuthenticationFailureListener implements
        ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    EventService eventService;

    @Autowired
    UserService userService;


    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {

        System.out.println("\n\n***FAILED LOGIN CACHED AT LISTENER***\n\n");

        try {
            String encoded = request.getHeader("Authorization");
            String decoded = new String(Base64.getDecoder().decode(encoded.split(" ")[1]));
            String formatted = decoded.split(":")[0];
            User user = userService.findByEmail(formatted);

            if(user != null) {
                    user.failedLogin();
                    userService.save(user);
            }

            Event event = new Event("LOGIN_FAILED",  formatted == null? "Anonymous" : formatted, request.getRequestURI(), request.getRequestURI());
            eventService.save(event);


            if(user != null && user.getFailedAttempts() > 4 && user.getActive() && !user.getRoles().contains("ROLE_ADMINISTRATOR")) {
                user.setActive(false);
                user.setFailedAttempts(0);
                userService.save(user);
                event = new Event("BRUTE_FORCE",  formatted == null? "Anonymous" : formatted, request.getRequestURI(), request.getRequestURI());
                eventService.save(event);
                event = new Event("LOCK_USER",  formatted == null? "Anonymous" : formatted, "Lock user " + formatted, "/api/admin/user/access");
                eventService.save(event);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            System.out.println("\n\n***FAILED LOGIN, FAILED LOGGING***\n\n");
            //e.printStackTrace();
        }
    }
}