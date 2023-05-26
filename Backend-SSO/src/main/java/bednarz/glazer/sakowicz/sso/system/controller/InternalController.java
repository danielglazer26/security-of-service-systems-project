package bednarz.glazer.sakowicz.sso.system.controller;

import bednarz.glazer.sakowicz.sso.system.controller.requests.UserInfoRequest;
import bednarz.glazer.sakowicz.sso.system.database.model.Person;
import bednarz.glazer.sakowicz.sso.system.database.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        var body = personService.getAllPeopleByIdsAndFilterApplicationName(userInfoRequest.usersId(), userInfoRequest.applicationName())
                .stream()
                .map(Person::toUserInfo)
                .toList();
        return ResponseEntity.ok(body);
    }
}
