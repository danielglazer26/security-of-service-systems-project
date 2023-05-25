package bednarz.glazer.sakowicz.sso.system.database.services;


import bednarz.glazer.sakowicz.sso.system.controller.requests.RegisterRequest;
import bednarz.glazer.sakowicz.sso.system.database.model.Person;
import bednarz.glazer.sakowicz.sso.system.database.model.ApplicationRoles;
import bednarz.glazer.sakowicz.sso.system.database.model.Roles;
import bednarz.glazer.sakowicz.sso.system.database.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PersonService {

    private final PersonRepository personRepository;

    private final ApplicationRolesService rolesService;

    @Autowired
    public PersonService(PersonRepository personRepository, ApplicationRolesService rolesService) {
        this.personRepository = personRepository;
        this.rolesService = rolesService;
    }

    public Optional<Person> createNewPerson(RegisterRequest registerRequest) {
        return createNewPerson(registerRequest.user(), registerRequest.pwd(), registerRequest.email());
    }

    public Optional<Person> createNewPerson(String username, String password, String email) {
        Optional<Person> optionalPerson = personRepository.findByUsername(username);
        if (optionalPerson.isEmpty()) {
            return Optional.of(personRepository.save(new Person(username, generateHash(password), email,
                    rolesService.getUserRoles())));
        } else {
            return Optional.empty();
        }
    }

    private static String generateHash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public Optional<Person> getPersonByLogin(String login) {
        return personRepository.findByUsername(login);
    }

    public Optional<Person> getPersonById(Long personId) {
        return personRepository.findById(personId);
    }

    public List<Person> getAllPeople() {
        return personRepository.findAll();
    }

    public void removePerson(Person person) {
        personRepository.delete(person);
    }

    public void removePeople(List<Person> person) {
        personRepository.deleteAll(person);
    }
}
