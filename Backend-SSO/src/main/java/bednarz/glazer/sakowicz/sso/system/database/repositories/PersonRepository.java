package bednarz.glazer.sakowicz.sso.system.database.repositories;


import bednarz.glazer.sakowicz.sso.system.database.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    @Override
    <S extends Person> S save(S entity);

    @Query("select p from Person p where p.login = ?1")
    Optional<Person> findByLogin(String login);

    @Query(nativeQuery = true, value = "select * from Person p where p.role != 0")
    List<Person> getAllUsers();
}