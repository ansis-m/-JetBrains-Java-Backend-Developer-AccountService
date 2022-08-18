package account.securityConfig;

import account.SecurityEvents.Event;
import account.SecurityEvents.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Autowired
    private EventService eventService;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        try {
            String encoded = request.getHeader("Authorization");
            String decoded = new String(Base64.getDecoder().decode(encoded.split(" ")[1]));
            String formatted = decoded.split(":")[0];
            Event event = new Event("LOGIN_FAILED",  formatted == "null"? "Anonymous" : formatted, request.getRequestURI(), request.getRequestURI());
            eventService.save(event);
        }
        catch (Exception e) {
            System.out.println("\n\n***FAILED LOGIN, FAILED LOGGING***\n\n");
            //e.printStackTrace();
        }

        System.out.println("\n\n\n***** EntryPoint - failed Login *****\n\n\n");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream().println(authException.getMessage());

        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());

    }
}
