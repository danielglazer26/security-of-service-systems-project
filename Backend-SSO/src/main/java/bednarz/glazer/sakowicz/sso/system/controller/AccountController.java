package bednarz.glazer.sakowicz.sso.system.controller;

import bednarz.glazer.sakowicz.sso.system.controller.requests.LoginRequest;
import bednarz.glazer.sakowicz.sso.system.controller.requests.RegisterRequest;
import bednarz.glazer.sakowicz.sso.system.controller.requests.ResponseJsonBody;
import bednarz.glazer.sakowicz.sso.system.controller.requests.UserInfoRequest;
import bednarz.glazer.sakowicz.sso.system.database.model.Person;
import bednarz.glazer.sakowicz.sso.system.database.services.AccountData;
import bednarz.glazer.sakowicz.sso.system.database.services.PersonService;
import bednarz.glazer.sakowicz.sso.system.settings.connection.jwt.CookieManager;
import bednarz.glazer.sakowicz.sso.system.settings.connection.otp.OtpManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AccountController {

    private final PersonService personService;
    private final AuthenticationManager authenticationManager;
    private final CookieManager cookieManager;
    private final OtpManager otpManager;

    @Autowired
    public AccountController(PersonService personService, AuthenticationManager authenticationManager,
                             CookieManager cookieManager, OtpManager otpManager) {
        this.personService = personService;
        this.authenticationManager = authenticationManager;
        this.cookieManager = cookieManager;
        this.otpManager = otpManager;
    }

    @GetMapping("/verify")
    public ResponseEntity<Long> verify(Authentication authentication) {
        AccountData accountData = (AccountData) authentication.getPrincipal();
        Person person = accountData.getPerson();
        ResponseCookie responseCookie = cookieManager.generateCookie(accountData.getUsername());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(person.getPersonId());
    }

    @PostMapping("/user/info")
    public ResponseEntity<?> usersInfo(@RequestBody UserInfoRequest userInfoRequest) {
        var body = personService.getAllPeopleByIdsAndFilterApplicationName(userInfoRequest.usersId(),
                userInfoRequest.applicationName()).stream()
                .map(Person::toUserInfo)
                .toList();
        return ResponseEntity.ok(body);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginToAccount(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.user(), loginRequest.pwd())
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseJsonBody("Incorrect credentials"));
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AccountData accountData = ((AccountData) authentication.getPrincipal());

        ResponseCookie responseCookie = cookieManager.generateOTPCookie(accountData.getUsername());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body("Correct credentials");
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNewUser(@Valid @RequestBody RegisterRequest registerRequest) {
        return personService.createNewPerson(registerRequest)
                .map(person -> ResponseEntity.ok(new ResponseJsonBody(otpManager.generateQRUrl(person))))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new ResponseJsonBody("This user exist: " + registerRequest.user()))
                );
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteAuthCookie = cookieManager.generateDeleteAuthCookies();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteAuthCookie.toString())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
