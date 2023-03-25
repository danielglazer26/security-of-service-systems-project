package bednarz.glazer.sakowicz.sso.system.controller;

import bednarz.glazer.sakowicz.sso.system.connection.settings.jwt.CookieManager;
import bednarz.glazer.sakowicz.sso.system.controller.requests.LoginRequest;
import bednarz.glazer.sakowicz.sso.system.controller.requests.RegisterRequest;
import bednarz.glazer.sakowicz.sso.system.controller.requests.ResponseJsonBody;
import bednarz.glazer.sakowicz.sso.system.database.model.Person;
import bednarz.glazer.sakowicz.sso.system.database.services.AccountData;
import bednarz.glazer.sakowicz.sso.system.database.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AccountController {
    public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    private final PersonService personService;
    private final AuthenticationManager authenticationManager;
    private final CookieManager cookieManager;

    @Autowired
    public AccountController(PersonService personService, AuthenticationManager authenticationManager, CookieManager cookieManager) {
        this.personService = personService;
        this.authenticationManager = authenticationManager;
        this.cookieManager = cookieManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginToAccount(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.login(), loginRequest.password())
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(new ResponseJsonBody("Incorrect credentials"));
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AccountData accountData = ((AccountData) authentication.getPrincipal());

        ResponseCookie responseCookie = cookieManager.generateOTPCookie(accountData.getUsername());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(personService.getPersonByLogin(loginRequest.login()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNewUser(@RequestBody RegisterRequest registerRequest) {
        String login = registerRequest.login();

        if (personService.getPersonByLogin(login).isEmpty()) {
            Optional<Person> newUser = personService.createNewPerson(
                    login,
                    BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt()),
                    registerRequest.email()
            );
            return checkUserCreation(newUser);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseJsonBody("This login exist: " + login));
    }

    private ResponseEntity<ResponseJsonBody> checkUserCreation(Optional<Person> newUser) {
        return newUser.map(person -> ResponseEntity.ok(new ResponseJsonBody(generateQRUrl(person))))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseJsonBody("Error occurs when add new user to database"))
                );
    }

    private String generateQRUrl(Person person) {
        return QR_PREFIX + URLEncoder.encode(
                String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                        "NAME1",
                        person.getEmail(),
                        person.getSecret(),
                        "NAME2"
                ), StandardCharsets.UTF_8
        );
    }

}
