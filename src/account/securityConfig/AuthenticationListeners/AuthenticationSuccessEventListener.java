package account.securityConfig.AuthenticationListeners;

import account.SecurityEvents.Event;
import account.user.User;
import account.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

@Component
public class AuthenticationSuccessEventListener implements
        ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    UserService userService;



    @Override
    public void onApplicationEvent(final AuthenticationSuccessEvent e) {

        try {
            String encoded = request.getHeader("Authorization");
            String decoded = new String(Base64.getDecoder().decode(encoded.split(" ")[1]));
            String formatted = decoded.split(":")[0];
            User user = userService.findByEmail(formatted);
            user.setFailedAttempts(0);
            userService.save(user);
            System.out.println("\n\n***RESET SUCCESSFULL!!!***\n\n");
        }
        catch (Exception exception) {
            System.out.println("\n\n***FAILED LOGIN RESET at Success Listener***\n\n");
            //e.printStackTrace();
        }

        System.out.println("\n\n***Sucsessfull event catched at listener***\n\n");
    }
}