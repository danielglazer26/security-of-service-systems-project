package bednarz.glazer.sakowicz.sso.system.database.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ApplicationRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private Roles role;

    @Column(nullable = false)
    private String applicationName;

    public ApplicationRoles(Roles role, String applicationName) {
        this.role = role;
        this.applicationName = applicationName;
    }
}
