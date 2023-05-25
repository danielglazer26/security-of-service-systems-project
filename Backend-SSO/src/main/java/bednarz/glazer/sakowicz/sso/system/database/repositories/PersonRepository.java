package bednarz.glazer.sakowicz.sso.system.database.repositories;


import bednarz.glazer.sakowicz.sso.system.database.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByUsername(String username);

    @Query("select p from Person p where p.personId in :peopleIds")
    List<Person> findAllByPeopleIds(List<Long> peopleIds);
}
