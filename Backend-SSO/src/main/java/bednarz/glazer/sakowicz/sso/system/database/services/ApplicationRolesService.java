package bednarz.glazer.sakowicz.sso.system.database.services;

import bednarz.glazer.sakowicz.sso.system.database.model.ApplicationRoles;
import bednarz.glazer.sakowicz.sso.system.database.model.Roles;
import bednarz.glazer.sakowicz.sso.system.database.repositories.ApplicationRolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ApplicationRolesService {

    private final ApplicationRolesRepository rolesRepository;

    @Autowired
    public ApplicationRolesService(ApplicationRolesRepository rolesRepository, @Value("${frontend.server.names}") List<String> clients) {
        this.rolesRepository = rolesRepository;

        clients.forEach(client -> Arrays.stream(Roles.values()).forEach(role -> {
            if (!rolesRepository.existsByRoleAndApplicationName(role, client)) {
                rolesRepository.save(new ApplicationRoles(role, client));
            }
        }));
    }

    public List<ApplicationRoles> getUserRoles() {
        return rolesRepository.findAllByRole(Roles.USER);
    }
}
