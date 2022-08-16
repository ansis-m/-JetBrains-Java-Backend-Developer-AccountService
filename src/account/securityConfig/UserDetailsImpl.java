package account.securityConfig;

import account.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class UserDetailsImpl implements UserDetails {

    private final String email;
    private final String password;
    private final List<GrantedAuthority> rolesAndAuthorities;


    UserDetailsImpl(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        rolesAndAuthorities = new ArrayList<GrantedAuthority>();
        for(String r : user.getRoles())
            rolesAndAuthorities.add(new SimpleGrantedAuthority(r));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return rolesAndAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
