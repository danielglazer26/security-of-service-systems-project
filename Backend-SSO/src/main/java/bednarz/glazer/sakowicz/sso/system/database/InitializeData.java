package bednarz.glazer.sakowicz.sso.system.database;

import bednarz.glazer.sakowicz.sso.system.database.model.ApplicationRole;
import bednarz.glazer.sakowicz.sso.system.database.model.Roles;
import bednarz.glazer.sakowicz.sso.system.database.services.ApplicationRolesService;
import bednarz.glazer.sakowicz.sso.system.database.services.PersonService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class InitializeData {

    private final ApplicationRolesService rolesService;
    private final PersonService personService;
    private final List<String> applicationNames;

    @Autowired
    public InitializeData(ApplicationRolesService rolesService, PersonService personService,
                          @Value("${frontend.server.names}") List<String> clients,
                          @Value("${sso.name}") String serverName) {
        this.rolesService = rolesService;
        this.personService = personService;
        applicationNames = clients;
        applicationNames.add(serverName);
    }

    @PostConstruct
    private void addData() {
        applicationNames.forEach(applicationName -> Arrays.stream(Roles.values()).forEach(role -> {
            if (!rolesService.checkIfApplicationRoleExists(role, applicationName)) {
                rolesService.saveApplicationRole(new ApplicationRole(role, applicationName));
            }
        }));

        personService.savePerson("admin", "admin", "admin@gmail.com", rolesService.getRolesForApplication(Roles.ADMIN));
        personService.savePerson("mod", "mod", "mod@gmail.com", rolesService.getRolesForApplication(Roles.MODERATOR));
    }
}
