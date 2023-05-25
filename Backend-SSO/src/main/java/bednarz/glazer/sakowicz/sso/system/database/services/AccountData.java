package bednarz.glazer.sakowicz.sso.system.database.services;


import bednarz.glazer.sakowicz.sso.system.database.model.ApplicationRole;
import bednarz.glazer.sakowicz.sso.system.database.model.Person;
import bednarz.glazer.sakowicz.sso.system.database.model.Roles;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AccountData implements UserDetails {

    @Getter
    private final Person person;

    private final Collection<? extends GrantedAuthority> authorities;

    public AccountData(Person person, String serverName) {
        this.person = person;
        authorities = getUserAuthority(serverName);
    }


    private List<GrantedAuthority> getUserAuthority(String serverName) {
        return List.of(
                new SimpleGrantedAuthority(
                        person.getRoles().stream()
                                .filter(applicationRoles -> applicationRoles.getApplicationName().equals(serverName))
                                .findFirst()
                                .orElse(new ApplicationRole(Roles.USER, serverName)).getRole().name()
                )
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return person.getPassword();
    }

    @Override
    public String getUsername() {
        return person.getUsername();
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
