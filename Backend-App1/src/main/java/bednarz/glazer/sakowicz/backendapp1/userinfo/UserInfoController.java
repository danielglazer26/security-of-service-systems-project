package bednarz.glazer.sakowicz.backendapp1.userinfo;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/info")
public class UserInfoController {
    @GetMapping
    public ResponseEntity<UserInfo> getUserInfo(Authentication authentication) {
        return ResponseEntity.ok((UserInfo) authentication.getPrincipal());
    }
}
