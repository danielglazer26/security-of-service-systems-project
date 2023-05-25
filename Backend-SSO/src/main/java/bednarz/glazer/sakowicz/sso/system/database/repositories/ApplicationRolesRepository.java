package bednarz.glazer.sakowicz.sso.system.database.repositories;


import bednarz.glazer.sakowicz.sso.system.database.model.ApplicationRoles;
import bednarz.glazer.sakowicz.sso.system.database.model.Person;
import bednarz.glazer.sakowicz.sso.system.database.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRolesRepository extends JpaRepository<ApplicationRoles, Long> {
    List<ApplicationRoles> findAllByRole(Roles role);

    boolean existsByRoleAndApplicationName(Roles role, String applicationName);

}
