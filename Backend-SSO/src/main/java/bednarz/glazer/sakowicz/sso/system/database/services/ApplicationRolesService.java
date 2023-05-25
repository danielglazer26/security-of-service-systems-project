package bednarz.glazer.sakowicz.sso.system.database.services;

import bednarz.glazer.sakowicz.sso.system.database.model.ApplicationRole;
import bednarz.glazer.sakowicz.sso.system.database.model.Roles;
import bednarz.glazer.sakowicz.sso.system.database.repositories.ApplicationRolesRepository;
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

    public boolean checkIfApplicationRoleExists(Roles roles, String applicationName) {
        return rolesRepository.existsByRoleAndApplicationName(roles, applicationName);
    }

    public void saveApplicationRole(ApplicationRole applicationRole) {
        rolesRepository.save(applicationRole);
    }

    public List<ApplicationRole> getRolesForApplication(String applicationName) {
        return rolesRepository.findAllByApplicationName(applicationName);
    }

    public List<ApplicationRole> getRolesForApplication(Roles role) {
        return rolesRepository.findAllByRole(role);
    }

    public Optional<ApplicationRole> getRolesForApplication(Roles roles, String applicationName) {
        return rolesRepository.findByRoleAndApplicationName(roles, applicationName);
    }
}
