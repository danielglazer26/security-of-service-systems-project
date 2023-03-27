package bednarz.glazer.sakowicz.sso.system;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConstStorage {
    public static final String ADDRESS_PROPERTIES = "address";
    public static final String AUTHENTICATED_ENDPOINT = "/authenticated";
    public static final String AUTHORIZATION_OTP_ENDPOINT = "/authorization/otp";
    public static final String COOKIE_NAME_PROPERTIES = "cookieName";
    public static final String JWT_SECRET_PROPERTIES = "jwt-secret";
}
