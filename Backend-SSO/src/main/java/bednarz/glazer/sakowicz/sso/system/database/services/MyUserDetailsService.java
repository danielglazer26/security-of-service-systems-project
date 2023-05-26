package bednarz.glazer.sakowicz.sso.system.database.services;


import bednarz.glazer.sakowicz.sso.system.database.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final PersonService personService;

    @Value("${app.name}")
    private String serverName;

    @Autowired
    public MyUserDetailsService(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<Person> person = personService.getPersonByLogin(username);
        if (person.isPresent()) {
            return new AccountData(person.get(), serverName);
        } else {
            throw new UsernameNotFoundException(username);
        }
    }
}
