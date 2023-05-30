package bednarz.glazer.sakowicz.sso.system.database.repositories;


import bednarz.glazer.sakowicz.sso.system.database.model.ApplicationRole;
import bednarz.glazer.sakowicz.ssolib.userinfo.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRolesRepository extends JpaRepository<ApplicationRole, Long> {
    List<ApplicationRole> findAllByRole(Role role);

    List<ApplicationRole> findAllByApplicationName(String applicationName);

    Optional<ApplicationRole> findByRoleAndApplicationName(Role roles, String applicationName);

    boolean existsByRoleAndApplicationName(Role role, String applicationName);

}
