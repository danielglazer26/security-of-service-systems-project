package bednarz.glazer.sakowicz.sso.system.database.services;

import bednarz.glazer.sakowicz.sso.system.database.model.ApplicationRole;
import bednarz.glazer.sakowicz.sso.system.database.repositories.ApplicationRolesRepository;
import bednarz.glazer.sakowicz.ssolib.userinfo.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationRolesService {

    private final ApplicationRolesRepository rolesRepository;

    @Autowired
    public ApplicationRolesService(ApplicationRolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    public boolean checkIfApplicationRoleExists(Role roles, String applicationName) {
        return rolesRepository.existsByRoleAndApplicationName(roles, applicationName);
    }

    public void saveApplicationRole(ApplicationRole applicationRole) {
        rolesRepository.save(applicationRole);
    }

    public List<ApplicationRole> getRolesForApplication(String applicationName) {
        return rolesRepository.findAllByApplicationName(applicationName);
    }

    public List<ApplicationRole> getRolesForApplication(Role role) {
        return rolesRepository.findAllByRole(role);
    }

    public Optional<ApplicationRole> getRolesForApplication(Role roles, String applicationName) {
        return rolesRepository.findByRoleAndApplicationName(roles, applicationName);
    }
}
