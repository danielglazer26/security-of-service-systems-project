package bednarz.glazer.sakowicz.sso.system.controller;

import bednarz.glazer.sakowicz.sso.system.connection.settings.jwt.CookieManager;
import bednarz.glazer.sakowicz.sso.system.controller.requests.ResponseJsonBody;
import bednarz.glazer.sakowicz.sso.system.database.model.Person;
import bednarz.glazer.sakowicz.sso.system.database.services.AccountData;
import jakarta.servlet.http.HttpServletRequest;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authorization/otp")
public class OtpController {

    private final CookieManager cookieManager;

    @Autowired
    public OtpController(CookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginToAccountOTP(HttpServletRequest request, @RequestParam String verificationCode) {
        AccountData accountData = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Person otpUser = accountData.getPerson();
        Totp totp = new Totp(otpUser.getSecret());

        if (checkOtpValidation(verificationCode, totp)) {
            return ResponseEntity.badRequest().body(new ResponseJsonBody("Invalid verification code"));
        }

        ResponseCookie responseCookie = cookieManager.generateCookie(request, accountData.getUsername());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(otpUser);
    }

    private boolean checkOtpValidation(String verificationCode, Totp totp) {
        return !isValidLong(verificationCode) || !totp.verify(verificationCode);
    }

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


}
