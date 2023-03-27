package bednarz.glazer.sakowicz.sso.system.connection.settings.otp;

import bednarz.glazer.sakowicz.sso.system.database.model.Person;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class OtpManager {

    private static final String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";

    public String generateQRUrl(Person person) {
        return QR_PREFIX + URLEncoder.encode(
                String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                        "NAME1",
                        person.getEmail(),
                        person.getSecret(),
                        "NAME2"
                ), StandardCharsets.UTF_8
        );
    }

    public boolean checkOtpValidation(String verificationCode, Person otpUser) {
        Totp totp = new Totp(otpUser.getSecret());
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
