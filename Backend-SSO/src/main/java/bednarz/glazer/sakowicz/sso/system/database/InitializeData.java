package bednarz.glazer.sakowicz.sso.system.database;

import bednarz.glazer.sakowicz.sso.system.database.model.ApplicationRole;
import bednarz.glazer.sakowicz.sso.system.database.model.Roles;
import bednarz.glazer.sakowicz.sso.system.database.services.ApplicationRolesService;
import bednarz.glazer.sakowicz.sso.system.database.services.PersonService;
import bednarz.glazer.sakowicz.sso.system.settings.connection.jwt.ApiKeysConfiguration;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static bednarz.glazer.sakowicz.sso.system.settings.connection.jwt.ApiKeysConfiguration.APP_NAME;

@Component
public class InitializeData {

    private final ApplicationRolesService rolesService;
    private final PersonService personService;
    private final List<String> applicationNames;

    @Autowired
    public InitializeData(ApplicationRolesService rolesService, PersonService personService,
                          ApiKeysConfiguration apiKeysConfiguration,
                          @Value("${app.name}") String serverName) {
        this.rolesService = rolesService;
        this.personService = personService;
        applicationNames = apiKeysConfiguration.getClients()
                .values()
                .stream().map(client -> client.get(APP_NAME))
                .collect(Collectors.toList());
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

        var user1Roles = List.of(
                rolesService.getRolesForApplication(Roles.USER, applicationNames.get(0)).get(),
                rolesService.getRolesForApplication(Roles.ADMIN, applicationNames.get(1)).get()
        );
        personService.savePerson("user1", "user1", "user1@gmail.com", user1Roles);

        var user2Roles = List.of(
                rolesService.getRolesForApplication(Roles.ADMIN, applicationNames.get(0)).get(),
                rolesService.getRolesForApplication(Roles.USER, applicationNames.get(1)).get()
        );
        personService.savePerson("user2", "user2", "user2@gmail.com", user2Roles);

        var user3Roles = List.of(
                rolesService.getRolesForApplication(Roles.MODERATOR, applicationNames.get(0)).get(),
                rolesService.getRolesForApplication(Roles.USER, applicationNames.get(1)).get()
        );
        personService.savePerson("user3", "user3", "user3@gmail.com", user3Roles);
    }
}
