package account.securityConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

//public class CustomAuthenticationFailureHandler
//        implements AuthenticationFailureHandler {
//

//
//    @Override
//    public void onAuthenticationFailure(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            AuthenticationException exception)
//            throws IOException, ServletException {
//
//        response.setStatus(HttpStatus.FORBIDDEN.value());
//        Map<String, Object> data = new HashMap<>();
//        data.put("timestamp", LocalDate.now());
//        data.put("error", "Forbidden");
//        data.put("status", 403);
//        data.put("message", "Access Denied!");
//        data.put("path", request.getRequestURI());
//
//        System.out.println("\n\n\nINSIDE CUSTOM!!!!\n\n\n");
//
//        response.getOutputStream()
//                .println(objectMapper.writeValueAsString(data));
//    }
//}

public class CustomAccessDeniedHandler implements AccessDeniedHandler {


    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc) throws IOException, ServletException {
                response.setStatus(HttpStatus.FORBIDDEN.value());
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", LocalDate.now());
        data.put("error", "Forbidden");
        data.put("status", 403);
        data.put("message", "Access Denied!");
        data.put("path", request.getRequestURI());

        System.out.println("\n\n\nINSIDE CUSTOM!!!!\n\n\n");

        response.getOutputStream()
                .println(objectMapper.writeValueAsString(data));
    }
}


//        "timestamp" : "<date>",
//        "status" : 403,
//        "error" : "Forbidden",
//        "message" : "Access Denied!",
//        "path" : "/api/admin/user/role"
