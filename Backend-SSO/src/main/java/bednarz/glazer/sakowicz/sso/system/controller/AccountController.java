package bednarz.glazer.sakowicz.sso.system.controller;

import bednarz.glazer.sakowicz.sso.system.connection.settings.jwt.CookieManager;
import bednarz.glazer.sakowicz.sso.system.connection.settings.otp.OtpManager;
import bednarz.glazer.sakowicz.sso.system.controller.requests.LoginRequest;
import bednarz.glazer.sakowicz.sso.system.controller.requests.RegisterRequest;
import bednarz.glazer.sakowicz.sso.system.controller.requests.ResponseJsonBody;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> registerNewUser(@RequestBody RegisterRequest registerRequest) {
        return personService.createNewPerson(registerRequest)
                .map(person -> ResponseEntity.ok(new ResponseJsonBody(otpManager.generateQRUrl(person))))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new ResponseJsonBody("This user exist: " + registerRequest.user()))
                );
    }

}
