package bednarz.glazer.sakowicz.sso.system.database.services;


import bednarz.glazer.sakowicz.sso.system.controller.requests.RegisterRequest;
import bednarz.glazer.sakowicz.sso.system.database.model.Person;
import bednarz.glazer.sakowicz.sso.system.database.model.Roles;
import bednarz.glazer.sakowicz.sso.system.database.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Optional<Person> createNewPerson(RegisterRequest registerRequest) {
        return createNewPerson(registerRequest.login(), registerRequest.password(), registerRequest.email());
    }

    public Optional<Person> createNewPerson(String login, String password, String email) {
        Optional<Person> optionalPerson = personRepository.findByLogin(login);
        if (optionalPerson.isEmpty()) {
            return Optional.of(personRepository.save(new Person(login, generateHash(password), email, Roles.USER)));
        } else {
            return Optional.empty();
        }
    }

    private static String generateHash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public Optional<Person> getPersonByLogin(String login) {
        return personRepository.findByLogin(login);
    }

    public Optional<Person> getPersonById(Long personId) {
        return personRepository.findById(personId);
    }

    public List<Person> getAllPeople() {
        return personRepository.findAll();
    }

    public List<Person> getAllUsers() {
        return personRepository.findByRole(Roles.USER);
    }

    public void removePerson(Person person) {
        personRepository.delete(person);
    }

    public void removePeople(List<Person> person) {
        personRepository.deleteAll(person);
    }
}
