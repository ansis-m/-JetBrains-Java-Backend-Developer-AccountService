package account.securityConfig;

import account.SecurityEvents.Event;
import account.SecurityEvents.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {


    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    EventService eventService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc) throws IOException, ServletException {
                response.setStatus(HttpStatus.FORBIDDEN.value());
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", LocalDate.now());
        data.put("error", "Forbidden");
        data.put("status", 403);
        data.put("message", "Access Denied!");
        data.put("path", request.getRequestURI());

        Event event = new Event("ACCESS_DENIED", request.getRemoteUser(), request.getRequestURI(), request.getRequestURI());
        eventService.save(event);

        System.out.println("\n\n\nINSIDE CUSTOM!!!!\n\n\n");

        response.getOutputStream()
                .println(objectMapper.writeValueAsString(data));
    }
}
