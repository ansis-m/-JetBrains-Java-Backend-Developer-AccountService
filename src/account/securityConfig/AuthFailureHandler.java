package account.securityConfig;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthFailureHandler {


    public static void onFailure(HttpServletRequest request, HttpServletResponse response) {
            System.out.println("\n\n\n****Failed Login!!!! Handler!*******\n\n\n");

    }
}
