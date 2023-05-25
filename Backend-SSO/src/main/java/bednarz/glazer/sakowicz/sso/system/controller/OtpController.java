package bednarz.glazer.sakowicz.sso.system.controller;

import bednarz.glazer.sakowicz.sso.system.settings.connection.jwt.CookieManager;
import bednarz.glazer.sakowicz.sso.system.settings.connection.otp.OtpManager;
import bednarz.glazer.sakowicz.sso.system.controller.requests.ResponseJsonBody;
import bednarz.glazer.sakowicz.sso.system.database.model.Person;
import bednarz.glazer.sakowicz.sso.system.database.services.AccountData;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authorization/otp")
public class OtpController {
    private final CookieManager cookieManager;
    private final OtpManager otpManager;

    @Autowired
    public OtpController(CookieManager cookieManager, OtpManager otpManager) {
        this.cookieManager = cookieManager;
        this.otpManager = otpManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginToAccountOTP(HttpServletRequest request, @RequestParam String verificationCode) {
        AccountData accountData = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Person otpUser = accountData.getPerson();
//        if (otpManager.checkOtpValidation(verificationCode, otpUser)) {
//            return ResponseEntity.badRequest().body(new ResponseJsonBody("Invalid verification code"));
//        }

        ResponseCookie deleteOTPCookie = cookieManager.generateDeleteOTPCookie();
        ResponseCookie responseCookie = cookieManager.generateCookie(request, accountData.getUsername());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString(), deleteOTPCookie.toString())
                .body(otpUser);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingParameterExceptions(MissingServletRequestParameterException ex) {
        return ex.getMessage();
    }
}
