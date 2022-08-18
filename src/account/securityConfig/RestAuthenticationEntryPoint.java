package account.securityConfig;

import account.SecurityEvents.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        System.out.println("\n\n\n***" + authException.getMessage() + "***\n\n\n");

        Map r = Map.of("timestamp", LocalDate.now().toString(), "status", 401, "error", "Unauthorized", "message", "User account is locked", "path", request.getRequestURI());

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(r);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream().println(json);
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
