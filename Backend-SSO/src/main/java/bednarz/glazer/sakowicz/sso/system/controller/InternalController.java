package bednarz.glazer.sakowicz.sso.system.controller;

import bednarz.glazer.sakowicz.sso.system.database.model.Person;
import bednarz.glazer.sakowicz.sso.system.database.services.PersonService;
import bednarz.glazer.sakowicz.sso.system.settings.connection.jwt.ApiKeysConfiguration;
import bednarz.glazer.sakowicz.ssolib.userinfo.UserInfoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/key/api")
public class InternalController {

    private final PersonService personService;

    @Autowired
    public InternalController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/user/info")
    public ResponseEntity<?> usersInfo(@RequestBody UserInfoRequest userInfoRequest) {
        Map<String, String> principal = (Map<String, String>) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var body = personService.getAllPeopleByIdsAndFilterApplicationName(userInfoRequest.usersId(),
                        principal.get(ApiKeysConfiguration.APP_NAME))
                .stream()
                .map(Person::toUserInfo)
                .toList();
        return ResponseEntity.ok(body);
    }
}
