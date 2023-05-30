package bednarz.glazer.sakowicz.sso.system.database.model;

import bednarz.glazer.sakowicz.ssolib.userinfo.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ApplicationRole {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String applicationName;

    public ApplicationRole(Role role, String applicationName) {
        this.role = role;
        this.applicationName = applicationName;
    }
}
