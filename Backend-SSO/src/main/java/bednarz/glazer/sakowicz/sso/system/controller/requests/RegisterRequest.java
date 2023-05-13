package bednarz.glazer.sakowicz.sso.system.controller.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @Email
        @NotNull
        String email,
        @Pattern(regexp = "^(?!.*\\s)(?!.*[~`!@#$%^&*()+={}\\[\\]|\\\\:;\"'<>,.?/₹]).{3,10}$")
        String user,
        @Pattern(regexp = "^(?!.*\\s)(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[~`!@#$%^&*()--+={}\\[\\]|\\\\:;\"'<>,.?/_₹]).{10,16}$")
        String pwd
) {}
