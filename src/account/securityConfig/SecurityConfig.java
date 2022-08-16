package account.securityConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder getEncoder() {
//        return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder(13);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{


        auth
                .userDetailsService(userDetailsService) // user store 1
                .passwordEncoder(getEncoder());

        auth.inMemoryAuthentication().withUser("ansis@acme.com").password(getEncoder().encode("ansis")).roles("USER")
                .and().withUser("agnis@acme.com").password(getEncoder().encode("agnis")).roles("ADMIN").and().passwordEncoder(getEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint) // Handle auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .authorizeRequests() // manage access
                .antMatchers(HttpMethod.POST, "/api/signup").permitAll()
                .mvcMatchers(HttpMethod.POST, "/api/auth/signup", "/api/acct/payments", "/actuator/shutdown").permitAll()
                .mvcMatchers(HttpMethod.PUT, "/api/acct/payments").permitAll()
                //.mvcMatchers(HttpMethod.GET, "/api/admin").hasRole("ADMIN")
                .mvcMatchers("/**").authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler());

    }
}
